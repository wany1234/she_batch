package com.batch.service;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.batch.utils.model.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.batch.mapper.LawMapper;
import com.batch.utils.ConstVal;

@Service
public class LawService {

    @Autowired
    private LawMapper lawMapper;

    private static final Logger logger = LoggerFactory.getLogger(LawService.class);

    /**
     * 개정법규 법제처 I/F
     */
    @Transactional
    public String lawElawIF() throws Exception {
        try {
            logger.info("### 법제처 코드 조회 ###");
            // 법제처코드(법)
            List<CodeMaster> law = this.lawMapper.getSelect(ConstVal.ELAW_TYPE);

            if (law != null && law.size() > 0) {
                for (CodeMaster codeMaster : law) {
                    // 법제처 open api를 통해 데이터 받아 저장
                    this.lawIF(codeMaster.getAttr1(), codeMaster.getCode());
                }
            }

            // 개정법규 상세 조회 대상 목록 조회
            List<LegiLawBasics> legiLawBasics = lawMapper.selectLawBasicList();

            // 개정법규 상세 저장
            for (LegiLawBasics lawBasic : legiLawBasics) {
                lawDetailIF(lawBasic.getLmst(), lawBasic.getEnfDate(), lawBasic.getLegiKey());
            }

            // 개정법규 상세 저장여부 업데이트
            lawMapper.updateLawElaw();

            return "SUCCESS";
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "FAIL";
        }
        return null;
    }

    // 최신개정법규 목록
    public int lawIF(String lawType, String lawId) throws Exception {
        BufferedReader br = null;
        // DocumentBuilderFactory 생성
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            String from = "";
            String to = "";
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            // 공포일 범위 1년 전 => 6년 전으로 변경
            cal.add(Calendar.YEAR, -6);
            from = df.format(cal.getTime());

            cal.add(Calendar.YEAR, 2);
            to = df.format(cal.getTime());

            List<String> paramList = new ArrayList<>();
            paramList.add("target=eflaw"); // 최신개정법규 목록
            paramList.add(String.format("OC=%s", ConstVal.LAW_IF_ID)); // 법제처 IF
                                                                       // ID
                                                                       // hyunkchm
            paramList.add("type=XML"); // XML 형태로 Return
            // paramList.add("display=100"); // 목록 표시 개수
            paramList.add("display=100&nw=2,3"); // 목록 표시 개수 및 예정+현행만 조회
            paramList.add(String.format("LID=%s", lawId)); // 법제처코드

            // paramList.add(String.format("nw=%s", "2,3"));
            // 1: 연혁, 2: 시행예정, 3: 현행 (기본값: 전체) : 차후 필요시 시행예정과 현행만 가져오게
            // 구성(20240104)
            // 연혁+예정 : nw=1,2
            // 예정+현행 : nw=2,3
            // 연혁+현행 : nw=1,3
            // 연혁+예정+현행 : nw=1,2,3
            paramList.add(String.format("ancYd=%s~%s", from, to));

            String params = String.join("&", paramList);

            // OpenApi호출
            String urlstr = String.format("http://www.law.go.kr/DRF/lawSearch.do?%s", params);

            System.out.println("URL: " + urlstr);
            URL url = new URL(urlstr);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

            // 응답 읽기
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                result = result + line.trim();// result = URL로 XML을 읽은 값
            }

            // xml 파싱하기
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

            String totalCntS = this.getTagValue("totalCnt", doc);

            int totalCnt = Integer.parseInt(totalCntS);

            if (totalCnt > 0) {
                int pageCount = (totalCnt / 100) + (totalCnt % 100 > 0 ? 1 : 0);
                for (int k = 0; k < pageCount; k++) {
                    String goUrl = urlstr;
                    goUrl = goUrl + "&page=" + (k + 1);
                    url = new URL(goUrl);
                    urlconnection = (HttpURLConnection) url.openConnection();

                    // 응답 읽기
                    br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
                    result = "";
                    line = "";
                    while ((line = br.readLine()) != null) {
                        result = result + line.trim();// result = URL로 XML을 읽은 값
                    }

                    // xml 파싱하기
                    is = new InputSource(new StringReader(result));
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(is);

                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();
                    XPathExpression expr = xpath.compile("/LawSearch/law");
                    NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        int resultNo = 0;
                        NodeList child = nodeList.item(i).getChildNodes();
                        LawElaw lawElaw = new LawElaw();
                        for (int j = 0; j < child.getLength(); j++) {
                            Node node = child.item(j);
                            switch (node.getNodeName()) {
                            case "법령일련번호":
                                lawElaw.setLmst(Integer.parseInt(node.getTextContent()));
                                break;
                            case "현행연혁코드":
                                lawElaw.setLstepNm(node.getTextContent());
                                break;
                            case "법령명한글":
                                lawElaw.setLnameKor(node.getTextContent());
                                break;
                            case "법령약칭명":
                                lawElaw.setLnameAbb(node.getTextContent());
                                break;
                            case "법령ID":
                                lawElaw.setLkey(node.getTextContent());
                                break;
                            case "공포일자":
                                lawElaw.setPromDate(node.getTextContent());
                                break;
                            case "공포번호":
                                lawElaw.setPromNum(node.getTextContent());
                                break;
                            case "제개정구분명":
                                lawElaw.setRevDiv(node.getTextContent());
                                break;
                            case "소관부처코드":
                                lawElaw.setMgrGovcd(node.getTextContent());
                                break;
                            case "소관부처명":
                                lawElaw.setMgrGov(node.getTextContent());
                                break;
                            case "법령구분명":
                                lawElaw.setLtypeNm(node.getTextContent());
                                break;
                            case "시행일자":
                                lawElaw.setEnfDate(node.getTextContent());
                                break;
                            case "자법타법여부":
                                lawElaw.setLflagNm(node.getTextContent());
                                break;
                            case "법령상세링크":
                                lawElaw.setLdtlLink(node.getTextContent());
                                break;
                            default:
                                // do something ...
                            }
                        }
                        lawElaw.setLegiKey(lawElaw.getLkey().concat(String.valueOf(lawElaw.getPromDate())).concat(String.valueOf(lawElaw.getPromNum())).concat(String.valueOf(lawElaw.getEnfDate())));
                        lawElaw.setLawTypeCd(lawType);
                        lawElaw.setSeqNo(lawMapper.getlawElawSeq());
                        lawMapper.mergeLawIF(lawElaw);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return 0;
    }

    /**
     * 최신개정법규 상세 법령 > 기본정보 법령 > 조문 > 항 > 호 > 목
     * 
     * @param lawId
     * @param enfDate
     * @return
     * @throws Exception
     */
    public int lawDetailIF(String lmst, String enfDate, String legiKey) throws Exception {
        BufferedReader br = null;
        // DocumentBuilderFactory 생성
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            List<String> paramList = new ArrayList<>();
            paramList.add("target=eflaw"); // 최신개정법규 상세
            paramList.add(String.format("OC=%s", ConstVal.LAW_IF_ID)); // 법제처 IF
                                                                       // ID
            paramList.add("type=XML"); // XML 형태로 Return
            paramList.add(String.format("MST=%s", lmst)); // 법령일련번호
            paramList.add(String.format("efYd=%s", enfDate)); // 시행일자

            String params = String.join("&", paramList);

            // OpenApi호출
            String urlstr = String.format("http://www.law.go.kr/DRF/lawService.do?%s", params);
            URL url = new URL(urlstr);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

            // 응답 읽기
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                result = result + line.trim();// result = URL로 XML을 읽은 값
            }

            // xml 파싱하기
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

            // 법령키 추출
            String lawDtl = doc.getDocumentElement().getAttribute("법령키");

            // 법령키가 있으면 xml 정보가 무사히 도착
            if (lawDtl != null && lawDtl.length() > 0) {

                // 기본정보 파싱
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                XPathExpression expr = xpath.compile("/법령/기본정보");
                NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                // 기본정보 모델
                LegiLawBasics legiLawBasics = new LegiLawBasics();

                // 기본정보의 태그수는 1개
                if (nodeList.getLength() == 1) {
                    NodeList child = nodeList.item(0).getChildNodes();
                    for (int i = 0; i < child.getLength(); i++) {
                        Node node = child.item(i);
                        switch (node.getNodeName()) {
                        case "법령ID":
                            legiLawBasics.setLegiId(node.getTextContent());
                            break;
                        case "공포일자":
                            legiLawBasics.setPromDate(node.getTextContent());
                            break;
                        case "공포번호":
                            legiLawBasics.setPromNum(node.getTextContent());
                            break;
                        case "언어":
                            legiLawBasics.setLang(node.getTextContent());
                            break;
                        case "법종구분":
                            legiLawBasics.setLtypeNm(node.getTextContent());
                            legiLawBasics.setLtypeCd(node.getAttributes().getNamedItem("법종구분코드").getTextContent());
                            break;
                        case "법령명_한글":
                            legiLawBasics.setLnameKor(node.getTextContent());
                            break;
                        case "법령명_한자":
                            legiLawBasics.setLnameZh(node.getTextContent());
                            break;
                        case "법령명_영어":
                            legiLawBasics.setLnameEng(node.getTextContent());
                            break;
                        case "법령명약칭":
                            legiLawBasics.setLnameAbb(node.getTextContent());
                            break;
                        case "편장절관":
                            legiLawBasics.setAttriSide(node.getTextContent());
                            break;
                        case "소관부처":
                            legiLawBasics.setMgrGov(node.getTextContent());
                            legiLawBasics.setMgrGovcd(node.getAttributes().getNamedItem("소관부처코드").getTextContent()); // 소관부처코드는
                                                                                                                     // 속성으로
                                                                                                                     // 조회
                            break;
                        case "시행일자":
                            legiLawBasics.setEnfDate(node.getTextContent());
                            break;
                        case "재개정구분":
                            legiLawBasics.setRevDiv(node.getTextContent());
                            break;
                        case "조문시행일자문자열":
                            legiLawBasics.setProvEnfDateTxt(node.getTextContent()); // 조문시행문자열은
                                                                                    // 기본정보에서만
                                                                                    // 조회
                            break;
                        case "별표시행일자문자열":
                            legiLawBasics.setAtfmEnfDateTxt(node.getTextContent());
                            break;
                        case "별표편집여부":
                            legiLawBasics.setAtfmEditFlag(node.getTextContent());
                            break;
                        case "공포법령여부":
                            legiLawBasics.setPromFlag(node.getTextContent());
                            break;
                        default:
                            // do something ...
                        }
                    }
                    // 기본정보 저장
                    legiLawBasics.setLegiKey(legiKey);
                    lawMapper.mergeLawBasicIF(legiLawBasics);
                }

                // 조문 파싱 (조문 > 항 > 호 > 목)
                expr = xpath.compile("/법령/조문");
                nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                // 조문 태그수는 1개
                if (nodeList.getLength() == 1) {
                    // 조문 태그의 하위 노드 조회
                    NodeList jomunList = nodeList.item(0).getChildNodes();

                    for (int i = 0; i < jomunList.getLength(); i++) {
                        NodeList jomunNodeList = jomunList.item(i).getChildNodes();
                        LegiLawJomun legiLawJomun = new LegiLawJomun();
                        legiLawJomun.setLegiKey(legiLawBasics.getLegiKey());
                        legiLawJomun.setProvKey(jomunList.item(i).getAttributes().getNamedItem("조문키").getTextContent());

                        int hangSeqNo = 1;

                        for (int jomunCnt = 0; jomunCnt < jomunNodeList.getLength(); jomunCnt++) {
                            Node jomunNode = jomunNodeList.item(jomunCnt);

                            // 법규정보 조문일 경우
                            if (!jomunNode.getNodeName().equals("항")) {
                                switch (jomunNode.getNodeName()) {
                                case "조문번호":
                                    legiLawJomun.setProvNum(jomunNode.getTextContent());
                                    break;
                                case "조문가지번호":
                                    legiLawJomun.setProvNumBran(jomunNode.getTextContent());
                                    break;
                                case "조문여부":
                                    legiLawJomun.setProvYn(jomunNode.getTextContent());
                                    break;
                                case "조문제목":
                                    legiLawJomun.setProvTitle(jomunNode.getTextContent());
                                    break;
                                case "조문시행일자":
                                    legiLawJomun.setProvEnfDate(jomunNode.getTextContent());
                                    break;
                                case "조문제개정유형":
                                    legiLawJomun.setProvRevType(jomunNode.getTextContent());
                                    break;
                                case "조문제개정유형문자열":
                                    legiLawJomun.setProvRevDateTxt(jomunNode.getTextContent()); // 소관부처
                                    break;
                                case "조문이동이전":
                                    legiLawJomun.setProvPrev(jomunNode.getTextContent());
                                    break;
                                case "조문이동이후":
                                    legiLawJomun.setProvNext(jomunNode.getTextContent());
                                    break;
                                case "조문변경여부":
                                    legiLawJomun.setProvChngYn(jomunNode.getTextContent());
                                    break;
                                case "조문내용":
                                    legiLawJomun.setProvContent(jomunNode.getTextContent());
                                    break;
                                case "조문참고자료":
                                    // 조문참고자료가 2개인 경우
                                    if (legiLawJomun.getProvRef() != null && Strings.isNotEmpty(legiLawJomun.getProvRef().trim())) {
                                        legiLawJomun.setProvRef2(jomunNode.getTextContent());
                                    } else {
                                        legiLawJomun.setProvRef(jomunNode.getTextContent());
                                    }
                                    break;
                                default:
                                    // do something ...
                                }
                            } else if (jomunNode.getNodeName().equals("항")) {
                                NodeList hangNodeList = jomunNode.getChildNodes();

                                LegiLawHang legiLawHang = new LegiLawHang();
                                legiLawHang.setLegiKey(legiLawBasics.getLegiKey());
                                legiLawHang.setProvKey(legiLawJomun.getProvKey());

                                int hoSeqNo = 1;

                                for (int hangCnt = 0; hangCnt < hangNodeList.getLength(); hangCnt++) {
                                    Node hangNode = hangNodeList.item(hangCnt);

                                    // 법규정보 항일 경우
                                    if (!hangNode.getNodeName().equals("호")) {
                                        switch (hangNode.getNodeName()) {
                                        case "항번호":
                                            legiLawHang.setClaNum(hangNode.getTextContent());
                                            break;
                                        case "항내용":
                                            legiLawHang.setClaContent(hangNode.getTextContent());
                                            break;
                                        case "항제개정유형":
                                            legiLawHang.setClaRevType(hangNode.getTextContent());
                                            break;
                                        case "항제개정일자문자열":
                                            legiLawHang.setClaRevDateTxt(hangNode.getTextContent());
                                            break;
                                        case "순번":
                                            legiLawJomun.setProvTitle(hangNode.getTextContent());
                                            break;
                                        default:
                                            // do something ...
                                        }
                                    } else if (hangNode.getNodeName().equals("호")) {
                                        NodeList hoNodeList = hangNode.getChildNodes();

                                        LegiLawHo legiLawHo = new LegiLawHo();
                                        legiLawHo.setLegiKey(legiLawBasics.getLegiKey());
                                        legiLawHo.setProvKey(legiLawJomun.getProvKey());
                                        legiLawHo.setClaNum(legiLawHang.getClaNum());

                                        int mokSeqNo = 1;

                                        for (int hoCnt = 0; hoCnt < hoNodeList.getLength(); hoCnt++) {
                                            Node hoNode = hoNodeList.item(hoCnt);

                                            // 법규정보 호일 경우
                                            if (!hoNode.getNodeName().equals("목")) {
                                                switch (hoNode.getNodeName()) {
                                                case "호번호":
                                                    legiLawHo.setNumbNum(hoNode.getTextContent());
                                                    break;
                                                case "호가지번호":
                                                    legiLawHo.setNumbNumBran(hoNode.getTextContent());
                                                    break;
                                                case "호내용":
                                                    legiLawHo.setNumbContent(hoNode.getTextContent());
                                                    break;
                                                default:
                                                    // do something ...
                                                }
                                            } else if (hoNode.getNodeName().equals("목")) {
                                                // 법규정보 목일 경우
                                                NodeList mokNodeList = hoNode.getChildNodes();

                                                LegiLawMok legiLawMok = new LegiLawMok();
                                                legiLawMok.setLegiKey(legiLawBasics.getLegiKey());
                                                legiLawMok.setProvKey(legiLawJomun.getProvKey());
                                                legiLawMok.setClaNum(legiLawHang.getClaNum());
                                                legiLawMok.setNumbNum(legiLawHo.getNumbNum());
                                                legiLawMok.setNumbNumBran(legiLawHo.getNumbNumBran());

                                                for (int mokCnt = 0; mokCnt < mokNodeList.getLength(); mokCnt++) {
                                                    Node mokNode = mokNodeList.item(mokCnt);

                                                    switch (mokNode.getNodeName()) {
                                                    case "목번호":
                                                        legiLawMok.setMokNum(mokNode.getTextContent());
                                                        break;
                                                    case "목내용":
                                                        legiLawMok.setMokContent(mokNode.getTextContent());
                                                        break;
                                                    default:
                                                        // do something ...
                                                    }
                                                }

                                                // 목 저장 (목번호가 있을 경우 저장)
                                                // legiLawMok.setSeqNo(mokSeqNo++);
                                                if (legiLawMok.getMokNum() != null && Strings.isNotEmpty(legiLawMok.getMokNum().trim())) {
                                                    lawMapper.mergeLawMokIf(legiLawMok);
                                                }
                                            }
                                        }

                                        // 호 저장 (호번호가 있을 경우 저장)
                                        // legiLawHo.setSeqNo(hoSeqNo++);
                                        if (legiLawHo.getNumbNum() != null && Strings.isNotEmpty(legiLawHo.getNumbNum().trim())) {
                                            lawMapper.mergeLawHoIf(legiLawHo);
                                        }
                                    }
                                }

                                // 항 저장 (항번호가 있을 경우 저장)
                                // legiLawHang.setSeqNo(hangSeqNo++);
                                if (legiLawHang.getClaNum() != null && Strings.isNotEmpty(legiLawHang.getClaNum().trim())) {
                                    lawMapper.mergeLawHangIf(legiLawHang);
                                }
                            }
                        }

                        // 조문 저장 (조문키가 있을 경우 저장)
                        if (legiLawJomun.getProvKey() != null && Strings.isNotEmpty(legiLawJomun.getProvKey().trim())) {
                            lawMapper.mergeLawJomunIf(legiLawJomun);
                        }
                    }
                }
            }
        } catch (IOException ie) {
            logger.error(ie.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return 0;
    }

    /**
     * 입법예고 정부입법지원센터 I/F
     */
    @Transactional
    public String lawMakingIF() {
        try {
            // logger.info("### 기관 코드 split ###");
            // 소관부처
            List<CodeMaster> orgCds = lawMapper.getSelect(ConstVal.LAW_MAKING_ORG);

            if (orgCds != null && orgCds.size() > 0) {
                for (CodeMaster orgCd : orgCds) {
                    // 법제처 open api를 통해 데이터 받아 저장
                    this.lawMakingIF(orgCd.getCode());
                }
            }
            return "SUCCESS";
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "FAIL";
        }
        return null;
    }

    // 입법예고법규
    public int lawMakingIF(String orgCd) throws Exception {
        BufferedReader br = null;
        // DocumentBuilderFactory 생성
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            // OpenApi호출
            // ConstVal.LAW_IF_ID
            String urlstr = "http://www.lawmaking.go.kr/rest/ogLmPp.xml?OC=" + ConstVal.LAW_IF_ID + "&diff=&cptOfiOrgCd=" + orgCd;
            String defailUrlstr = "https://www.lawmaking.go.kr/rest/ogLmPp/{0}/{1}/{2}.html?OC=" + ConstVal.LAW_IF_ID;
            String detailXmlUrlStr = "https://www.lawmaking.go.kr/rest/ogLmPp/%s/%s/%s.xml?OC=" + ConstVal.LAW_IF_ID;
            URL url = new URL(urlstr);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

            // 응답 읽기
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                result = result + line.trim();// result = URL로 XML을 읽은 값
            }

            // xml 파싱하기
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

            String retMsgS = this.getTagValue("retMsg", doc);
            String totalCntS = this.getTagValue("totalCnt", doc);
            String pageIndexS = this.getTagValue("pageIndex", doc);
            String pageSizeS = this.getTagValue("pageSize", doc);

            int retMsg = Integer.parseInt(retMsgS);
            int totalCnt = Integer.parseInt(totalCntS);
            int pageIndex = Integer.parseInt(pageIndexS);
            int pageSize = Integer.parseInt(pageSizeS);

            if (totalCnt > 0 && pageSize > 0) {
                int pageCount = (totalCnt / pageSize) + (totalCnt % pageSize > 0 ? 1 : 0);
                for (int k = 0; k < pageCount; k++) {
                    String goUrl = urlstr;
                    goUrl = goUrl + "&pageIndex=" + (k + 1);
                    url = new URL(goUrl);
                    urlconnection = (HttpURLConnection) url.openConnection();

                    // 응답 읽기
                    br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
                    result = "";
                    line = "";
                    while ((line = br.readLine()) != null) {
                        result = result + line.trim();// result = URL로 XML을 읽은 값
                    }

                    // xml 파싱하기
                    is = new InputSource(new StringReader(result));
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(is);

                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();
                    XPathExpression expr = xpath.compile("/result/list/ApiList04Vo");
                    NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NodeList child = nodeList.item(i).getChildNodes();
                        LawMaking lawMaking = new LawMaking();
                        for (int j = 0; j < child.getLength(); j++) {
                            Node node = child.item(j);
                            switch (node.getNodeName()) {
                            case "ogLmPpSeq":
                                lawMaking.setOgLmPpSeq(node.getTextContent());
                                break;
                            case "lsNm":
                                lawMaking.setIsNm(node.getTextContent());
                                break;
                            case "lsClsNm":
                                lawMaking.setIsClsNm(node.getTextContent());
                                break;
                            case "asndOfiNm":
                                lawMaking.setAsndOfiNm(node.getTextContent());
                                break;
                            case "pntcNo":
                                lawMaking.setPntcNo(node.getTextContent());
                                break;
                            case "pntcDt":
                                lawMaking.setPntcDt(node.getTextContent());
                                break;
                            case "stYd":
                                lawMaking.setStYd(node.getTextContent());
                                break;
                            case "edYd":
                                lawMaking.setEdYd(node.getTextContent());
                                break;
                            case "FileName":
                                lawMaking.setFileName(node.getTextContent());
                                break;
                            case "FileDownLink":
                                lawMaking.setFileDownLink(node.getTextContent());
                                break;
                            case "readCnt":
                                lawMaking.setReadCnt(Integer.parseInt(node.getTextContent()));
                                break;
                            case "mappingLbicId":
                                lawMaking.setMappingLbicId(node.getTextContent());
                                break;
                            case "announceType":
                                lawMaking.setAnnounceType(node.getTextContent());
                                break;
                            default:
                                // do something ...
                            }

                        }
                        String tranDefailUrlstr = defailUrlstr;
                        lawMaking.setDetailUrl(tranDefailUrlstr.replace("{0}", lawMaking.getOgLmPpSeq()).replace("{1}", lawMaking.getMappingLbicId()).replace("{2}", lawMaking.getAnnounceType()));
                        lawMaking.setMgrGovcd(orgCd);
                        lawMaking.setPntcDt(this.getFormat(lawMaking.getPntcDt()));
                        lawMaking.setStYd(this.getFormat(lawMaking.getStYd()));
                        lawMaking.setEdYd(this.getFormat(lawMaking.getEdYd()));
                        lawMaking.setSeqNo(lawMapper.getlawMakingSeq());

                        url = new URL(String.format(detailXmlUrlStr, lawMaking.getOgLmPpSeq(), lawMaking.getMappingLbicId(), lawMaking.getAnnounceType()));
                        urlconnection = (HttpURLConnection) url.openConnection();

                        // 응답 읽기
                        br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
                        result = "";
                        line = "";
                        while ((line = br.readLine()) != null) {
                            result = result + line.trim();// result = URL로 XML을
                                                          // 읽은 값
                        }

                        // xml 파싱하기
                        is = new InputSource(new StringReader(result));
                        builder = factory.newDocumentBuilder();
                        doc = builder.parse(is);

                        lawMaking.setDetailContents(getTagValue("lmPpCts", doc));

                        lawMapper.mergeLawMakingIF(lawMaking);
                    }
                }

            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return 0;
    }

    private static String getTagValue(String tag, Document doc) {
        NodeList nlList = doc.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if (nValue == null) {
            return null;
        }
        return nValue.getNodeValue();
    }

    private static String getFormat(String ymd) {
        // 2021.04.15 이진규 작성
        String orgYmd = ymd;
        int firstDot = orgYmd.indexOf(".");
        int secondDot = orgYmd.indexOf(".", firstDot + 1);
        if (orgYmd.length() == 13) {
            // "2021. 10. 10."
            ymd = ymd.substring(0, ymd.length() - 1).replaceAll(". ", "-");
        } else if (orgYmd.length() == 12) {
            if (secondDot == 7) {
                // "2021. 2. 10."
                ymd = ymd.substring(0, firstDot) + "-0" + ymd.substring(firstDot + 2, firstDot + 3) + "-" + ymd.substring(secondDot + 2, secondDot + 4);
            } else {
                // "2021. 10. 2."
                ymd = (ymd.substring(0, secondDot) + "-0" + ymd.substring(secondDot + 2, secondDot + 3)).replaceAll(". ", "-");
            }
        } else if (orgYmd.length() == 11) {
            // "2021. 1. 1."
            ymd = ymd.substring(0, firstDot) + "-0" + ymd.substring(firstDot + 2, firstDot + 3) + "-0" + ymd.substring(secondDot + 2, secondDot + 3);
        }
        return ymd;
    }

}
