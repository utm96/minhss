package src;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import src.model.CheckCodeRunable;
import src.model.DataFrame;
import src.model.Info;
import src.model.ResponseTcbs;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class MyGETRequest {
    private static final Logger LOGGER = LogManager.getLogger(MyGETRequest.class);

    public static DataFrame checkRequestBuy(StringBuffer response, int i, int threshHold) throws IOException {
        if (response != null) {
            // print result
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            if (responseTcbs.getData().size() >= 500 + i) {
                List<DataFrame> data50 = responseTcbs.getData().subList(responseTcbs.getData().size() - 500 - i, responseTcbs.getData().size() - i);
//                System.out.println("JSON String Result " + response.toString());

                //todo get ma50
                Double vol50 = caculateVol50(data50);
                DataFrame lastestRecord = data50.get(499);
                Double lastestVol = lastestRecord.getVolume();
                //todo detect candle
                Boolean checkCandle = checkCandleValueBuy(lastestRecord);

                //todo push noti tele
                //GetAndPost.POSTRequest(response.toString());
                try {
                    if (checkCandle && lastestVol > 3 * vol50 && lastestVol * lastestRecord.getLow() > 1000000000d * threshHold) {
                        lastestRecord.setPoint(lastestVol / vol50);
                        lastestRecord.setSymbol(responseTcbs.getTicker());
                        return lastestRecord;
//                        return responseTcbs.getTicker() + "- lastestVol: " + lastestVol
//                                + "- closePrice: " + lastestRecord.getClose() + "time: " + lastestRecord.getTradingDate();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("GET NOT WORKED");
        }
        return null;
    }

    public static DataFrame checkRequestBuyDay(StringBuffer response, int i, int vol, Info info) throws IOException {
        if (response != null) {
            // print result
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            if (responseTcbs.getData().size() >= vol + i) {
                List<DataFrame> data50 = responseTcbs.getData().subList(responseTcbs.getData().size() - vol - i, responseTcbs.getData().size() - i);
//                System.out.println("JSON String Result " + response.toString());
                for (DataFrame dataFrame : data50) {
                    if (checkPf(dataFrame)) {
                        info.setPf(info.getPf() + 1);
                    }
                }
                //todo get ma50
                Double vol50 = caculateVol50(data50);
                DataFrame lastestRecord = data50.get(vol - 1);
                Double lastestVol = lastestRecord.getVolume();
                //todo detect candle
//                Boolean checkCandle = checkCandleValueBuy(lastestRecord);

                //todo push noti tele
                //GetAndPost.POSTRequest(response.toString());
                try {
                    if (lastestVol < vol50) {
                        lastestRecord.setPoint(lastestVol / vol50);
                        lastestRecord.setSymbol(responseTcbs.getTicker());
                        return lastestRecord;
//                        return responseTcbs.getTicker() + "- lastestVol: " + lastestVol
//                                + "- closePrice: " + lastestRecord.getClose() + "time: " + lastestRecord.getTradingDate();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("GET NOT WORKED");
        }
        return null;
    }

    public static boolean checkPf(DataFrame dataFrame) {
        if ((dataFrame.getHigh() - dataFrame.getClose()) / dataFrame.getOpen() <= 0.01) {
            return true;
        }
        return false;
    }

    public static DataFrame checkRequestSell(StringBuffer response, int i) throws IOException {
        if (response != null) {
            // print result
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            if (responseTcbs.getData().size() >= 20 + i) {
                List<DataFrame> data50 = responseTcbs.getData().subList(responseTcbs.getData().size() - 20 - i, responseTcbs.getData().size() - i);
//                System.out.println("JSON String Result " + response.toString());
                //todo get ma50
                Double vol50 = caculateVol50(data50);
                DataFrame lastestRecord = data50.get(19);
                Double lastestVol = lastestRecord.getVolume();
                //todo detect candle
                Boolean checkCandle = checkCandleValueSell(lastestRecord);

                //todo push noti tele
                //GetAndPost.POSTRequest(response.toString());
                try {
                    if (checkCandle && lastestVol > 2 * vol50
//                            && lastestVol * lastestRecord.getLow() > 1000000000d * 3
                    ) {
                        lastestRecord.setPoint(lastestVol / vol50);
                        lastestRecord.setSymbol(responseTcbs.getTicker());
                        return lastestRecord;
//                        return responseTcbs.getTicker() + "- lastestVol: " + lastestVol
//                                + "- closePrice: " + lastestRecord.getClose() + "time: " + lastestRecord.getTradingDate();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("GET NOT WORKED");
        }
        return null;
    }

    public static DataFrame checkCashFlowByWeek(String param, int i) throws IOException {
        String url = "https://apipubaws.tcbs.com.vn/stock-insight/v1/stock/bars?" + param;
        System.out.println(url);
        URL urlForGetRequest = new URL(url);
//        System.out.println(urlForGetRequest.get);
        String readLine = null;
//        List<DataFrame> result = new ArrayList<DataFrame>();
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
//        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();


        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            // print result
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            if (responseTcbs.getData().size() >= 20 - i) {
                List<DataFrame> data20 = responseTcbs.getData().subList(responseTcbs.getData().size() - 20 - i, responseTcbs.getData().size() - i);
//                System.out.println("JSON String Result " + response.toString());
                //todo get ma50
                Double vol20 = caculateVolI(data20, 20);
                DataFrame lastestRecord = data20.get(19);
                Double lastestVol = lastestRecord.getVolume();
                //todo detect candle
//                Boolean checkCandle = checkCandleValue(lastestRecord);

                //todo push noti tele
                //GetAndPost.POSTRequest(response.toString());
                try {
                    if (lastestVol > 2 * vol20) {
                        lastestRecord.setSymbol(responseTcbs.getTicker());
                        return lastestRecord;
//                        return responseTcbs.getTicker() + "- lastestVol: " + lastestVol
//                                + "- closePrice: " + lastestRecord.getClose() + "time: " + lastestRecord.getTradingDate();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("GET NOT WORKED");
        }
        return null;
    }

    public static StringBuffer callRequest(String param) throws Exception {
        String url = "https://apipubaws.tcbs.com.vn/stock-insight/v1/stock/bars?" + param;
        System.out.println(url);
        URL urlForGetRequest = new URL(url);
//        System.out.println(urlForGetRequest.get);
        String readLine = null;
//        List<DataFrame> result = new ArrayList<DataFrame>();
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
//        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            return response;
        } else {
            return null;
        }
    }

    public static StringBuffer callRequestDay(String param) throws Exception {
        String url = "https://apipubaws.tcbs.com.vn/stock-insight/v1/stock/bars-long-term?" + param;
        System.out.println(url);
        URL urlForGetRequest = new URL(url);
//        System.out.println(urlForGetRequest.get);
        String readLine = null;
//        List<DataFrame> result = new ArrayList<DataFrame>();
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
//        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            return response;
        } else {
            return null;
        }
    }

    private static Boolean checkCandleValueBuy(DataFrame lastestRecord) {
        Double high = lastestRecord.getHigh();
        Double low = lastestRecord.getLow();
        Double close = lastestRecord.getClose();
        Double open = lastestRecord.getOpen();
        Double cellingCandle = Math.min(close, open);
        Double tailCandle = cellingCandle - low;
        Double candleLength = high - low;
        if (tailCandle / candleLength >= 0.5) {
//            System.out.println("OK");
            return true;
        }
//        System.out.println("OK");

        return false;
    }

    private static Boolean checkCandleValueSell(DataFrame lastestRecord) {
        Double high = lastestRecord.getHigh();
        Double low = lastestRecord.getLow();
        Double close = lastestRecord.getClose();
        Double open = lastestRecord.getOpen();
        Double max = Math.max(close, open);
//        Double min = Math.min()
//        {"open":53300.00,"high":53400.00,"low":53000.00,"close":53000.00,
//        "volume":58500,"tradingDate":"2021-11-19T03:00:00.000Z"},
        if (high.equals(53400d)) {
            System.out.println(high);
            System.out.println(low);
            System.out.println(open);
            System.out.println(close);
        }
        Double tailCandle = high - max;
        Double candleLength = high - low;
        if (tailCandle / candleLength >= 0.2 || candleLength.equals(0d)) {
//            System.out.println("OK");
            return true;
        }
//        System.out.println("OK");

        return false;
    }

    private static Double caculateVol50(List<DataFrame> data50) {
        Double sum = 0d;
        for (DataFrame dataFrame : data50) {
            sum += dataFrame.getVolume();
        }
        return sum / data50.size();
    }

    private static Double caculateVolI(List<DataFrame> data50, int i) {
        Double sum = 0d;
        for (DataFrame dataFrame : data50) {
            sum += dataFrame.getVolume();
        }
        return sum / i;
    }

    public static void main(String[] args) throws IOException {
//        Map<String, String> key = new HashMap<String, String>();
//        key.put("type", "stock");
//        key.put("resolution", "5");
//        Long nowTime = new Date().getTime() / 1000;
//        Long fromTime = nowTime - 60 * 60 * 24 * 200;
//        key.put("from", fromTime.toString());
//        key.put("to", nowTime.toString());
//        FileConverToObject fileConverToObject = new FileConverToObject();
        int threshHold = 3;
        int volDefault = 20;
        int dayLast = 3;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String outputPath = "minhut" + simpleDateFormat.format(new Date()) + ".xlsx";
        int corePoolSize = 160;
        int maxPoolSize = 3500;
        int keepAlive = 10000;
        int responseSize = 2000;

        String listCodeToCheck = "AAV,ABT,ACM,ADC,AGC,ALT,AMC,AME,AMV,API,APP,APS,ARM,ART,ASG,ATS,AVS,BAB,BAX,BBC,BBS,BCC,BCF,BDB,BED,BII,BKC,BLF,BNA,BPC,BSC,BSI,BST,BTS,BTW,BVS,BXH,C69,C92,CAG,CAN,CAP,CAV,CDN,CEO,CET,CIA,CIC,CJC,CKV,CLH,CLM,CMC,CMS,CPC,CSC,CTB,CTC,CTM,CTP,CTT,CTV,CTX,CVN,CX8,D11,DAD,DAE,DC2,DDG,DGL,DHI,DHL,DHP,DHT,DIH,DL1,DNC,DNM,DNP,DP3,DPC,DS3,DST,DTD,DTK,DVG,DXP,DXS,DZM,E1SSHN30,EBA,EBS,ECI,EID,EVS,FDT,FID,FTV,GBS,GDW,GFC,GHA,GIC,GKM,GLT,GMA,GMX,HAD,HAP,HAT,HBB,HBE,HBS,HCC,HCT,HDA,HEV,HGM,HHC,HHG,HHL,HJS,HKT,HLC,HLD,HMH,HNM,HOM,HPC,HPM,HPR,HPS,HSC,HST,HTB,HTC,HTP,HUT,HVT,ICG,IDC,IDJ,IDV,INC,INN,ITQ,IVS,KBT,KDM,KHG,KHS,KKC,KLF,KLS,KMF,KMT,KSD,KSQ,KST,KTS,KTT,KVC,L14,L18,L35,L40,L43,L61,L62,LAF,LAS,LBE,LCD,LCS,LDP,LHC,LIG,LM7,LUT,MAC,MAS,MAX,MBG,MBS,MCC,MCF,MCL,MCO,MDC,MED,MEL,MHL,MIH,MIM,MKV,MNC,MSC,MST,MVB,NAG,NAP,NBC,NBP,NBW,NDN,NDX,NET,NFC,NHC,NIS,NLC,NRC,NSC,NSH,NSN,NST,NTH,NTP,NVB,NVC,OCH,ONE,PAN,PBP,PCE,PCG,PCT,PDB,PDC,PEN,PGN,PGS,PGT,PHN,PHP,PIA,PIC,PJC,PLC,PMB,PMC,PMP,PMS,POT,PPE,PPP,PPS,PPY,PRC,PRE,PSC,PSD,PSE,PSI,PSW,PTD,PTI,PTS,PV2,PVB,PVC,PVG,PVI,PVL,PVS,QBS,QHD,QST,QTC,RCL,RHC,S55,S64,S91,S99,SAF,SCI,SD2,SD4,SD5,SD6,SD9,SDA,SDC,SDG,SDN,SDS,SDT,SDU,SEB,SED,SEL,SFN,SGC,SGD,SGH,SHB,SHE,SHN,SHS,SHT,SIC,SJ1,SJE,SKS,SLS,SME,SMN,SMT,SNG,SPI,SRA,SSC,SSM,SSS,STC,STP,SVN,SVS,SZB,TA9,TAR,TAS,TBX,TC6,TDN,TDT,TET,TFC,THB,THD,THI,THS,THT,THV,TIG,TJC,TKC,TKU,TLC,TMB,TMC,TMX,TNG,TPH,TPP,TSB,TSM,TST,TTC,TTH,TTL,TTT,TTZ,TV3,TV4,TVB,TVC,TVD,TXM,UNI,V12,V21,VAT,VBC,VC1,VC2,VC3,VC6,VC7,VC9,VCC,VCH,VCM,VCS,VCV,VDL,VE1,VE2,VE3,VE4,VE8,VFG,VGP,VGS,VHE,VHL,VIE,VIF,VIG,VIT,VKC,VLA,VMC,VMS,VNC,VND,VNF,VNR,VNT,VSA,VSM,VTC,VTH,VTJ,VTL,VTV,VXB,WCS,WSS,X20,YSC,AAA,AAM,AAT,ABS,ACB,ACC,ACL,ADG,ADS,AGD,AGG,AGM,AGR,AHP,ALP,AMD,ANC11601,ANV,APC,APG,APH,ASIAGF,ASM,ASP,AST,BAS,BCE,BCG,BCI,BCM,BFC,BGM,BHN,BHS,BIC,BID,BKG,BMC,BMI,BMP,BRC,BTP,BTT,BVH,BWE,C32,C47,CCI,CCL,CDC,CEE,CFPT2016,CFPT2101,CHP,CHPG2020,CHPG2101,CHPG2102,CHPG2103,CHPG2104,CHPG2105,CHPG2106,CHPG2107,CHPG2108,CIG,CII,CII11709,CII41401,CKDH2002,CKDH2101,CKDH2102,CKG,CLC,CLL,CLP,CLW,CMBB2010,CMBB2101,CMG,CMSN2101,CMSN2102,CMSN2103,CMV,CMWG2013,CMWG2016,CMWG2101,CMWG2102,CMWG2103,CMWG2104,CMWG2105,CMX,CNG,CNVL2003,CNVL2101,CNVL2102,COM,CPDR2101,CPDR2102,CPNJ2101,CPNJ2102,CPNJ2103,CRC,CRE,CREE2101,CSBT2101,CSG,CSM,CSTB2007,CSTB2010,CSTB2014,CSTB2101,CSTB2102,CSTB2103,CSTB2104,CSV,CTCB2012,CTCB2101,CTCB2102,CTCB2103,CTCB2104,CTCH2003,CTCH2102,CTCH2103,CTD,CTF,CTG,CTI,CTS,CVHM2008,CVHM2101,CVHM2102,CVHM2103,CVHM2104,CVHM2105,CVHM2106,CVIC2005,CVIC2101,CVIC2102,CVIC2103,CVJC2006,CVNM2011,CVNM2101,CVNM2102,CVNM2103,CVNM2104,CVNM2105,CVPB2015,CVPB2101,CVPB2102,CVPB2103,CVPB2104,CVRE2009,CVRE2011,CVRE2013,CVRE2101,CVRE2102,CVRE2103,CVRE2104,CVT,D2D,DAG,DAH,DAT,DBC,DBD,DBT,DC4,DCC,DCL,DCM,DGC,DGW,DHA,DHC,DHG,DHM,DIG,DLG,DMC,DPG,DPM,DPR,DQC,DRC,DRH,DRL,DSN,DTA,DTL,DTT,DVD,DVP,DXG,DXV,E1VFVN30,EIB,ELC,EMC,EVE,EVG,FBT,FCM,FCN,FDC,FIR,FIT,FLC,FMC,FPC,FPT,FRT,FTM,FTS,FUCTVGF1,FUCTVGF2,FUCVREIT,FUEMAV30,FUESSV30,FUESSV50,FUESSVFL,FUEVFVND,FUEVN100,GAB,GAS,GDT,GEG,GEX,GIL,GMC,GMD,GSP,GTA,GTN,GVR,HAG,HAH,HAI,HAR,HAS,HAX,HBC,HCD,HCM,HDB,HDC,HDG,HHP,HHS,HID,HII,HMC,HNG,HOT,HPG,HPX,HQC,HRC,HSG,HSL,HT1,HT2,HTI,HTL,HTN,HTV,HU1,HU3,HUB,HVH,HVN,HVX,IBC,ICT,IDI,IJC,ILB,IMP,ITA,ITC,ITD,JVC,KBC,KDC,KDH,KHP,KMR,KOS,KPF,KSB,L10,LBM,LCG,LCM,LDG,LEC,LGC,LGL,LHG,LIX,LM8,LPB,LSS,MAFPF1,MBB,MCG,MCP,MCV,MDG,MHC,MIG,MSB,MSH,MSN,MWG,NAF,NAV,NBB,NCT,NHA,NHH,NHS,NHW,NKD,NKG,NLG,NNC,NT2,NTL,NVL,NVN,NVT,OCB,OGC,OPC,PAC,PC1,PDN,PDR,PET,PGC,PGD,PGI,PHC,PHR,PHT,PIT,PJT,PLP,PLX,PME,PMG,PNC,PNJ,POM,POW,PPC,PRUBF1,PSH,PTB,PTC,PTL,PVD,PVF,PVT,PXI,PXS,QCG,RAL,RDP,REE,RIC,ROS,S4A,SAB,SAM,SAV,SBA,SBC,SBT,SBV,SC5,SCD,SCR,SCS,SEC,SFC,SFG,SFI,SGN,SGR,SGT,SHA,SHI,SHP,SII,SJD,SJF,SJS,SKG,SMA,SMB,SMC,SPM,SRC,SRF,SSB,SSI,ST8,STB,STG,STK,SVC,SVD,SVI,SVT,SZC,SZL,TAC,TBC,TCB,TCD,TCH,TCL,TCM,TCO,TCR,TCT,TDC,TDG,TDH,TDM,TDP,TDW,TEG,TGG,THG,TIC,TIP,TIX,TLD,TLG,TLH,TMP,TMS,TMT,TN1,TNA,TNC,TNH,TNI,TNT,TPB,TPC,TRA,TRC,TRI,TS4,TSC,TTA,TTB,TTE,TTF,TV2,TVS,TVT,TYA,UDC,UIC,VAF,VCA,VCB,VCF,VCG,VCI,VDP,VDS,VFMVF1,VFMVF4,VFMVFA,VFMVN30,VGC,VHC,VHM,VIB,VIC,VIC11501,VIC11504,VID,VIP,VIS,VIX,VJC,VMD,VNE,VNG,VNL,VNM,VNS,VOS,VPB,VPD,VPG,VPH,VPI,VPL,VPS,VRC,VRE,VSC,VSH,VSI,VTB,VTF,VTO,YBM,YEG,A32,AAS,ABB,ABC,ABI,ABR,AC4,ACE,ACG,ACS,ACV,ADP,AFC,AFX,AG1,AGF,AGP,AGX,AIC,ALV,AMP,AMS,ANT,APF,APL,APT,AQN,ASA,ASD,ATA,ATB,ATD,ATG,AUM,AVC,AVF,B82,BAL,BAM,BBH,BBM,BBT,BCB,BCP,BCV,BDC,BDF,BDG,BDT,BDW,BEL,BGW,BHA,BHC,BHG,BHK,BHP,BHT,BHV,BIO,BKH,BLI,BLN,BLT,BLU,BLW,BM9,BMD,BMF,BMG,BMJ,BMN,BMS,BMV,BNW,BOT,BPW,BQB,BRR,BRS,BSA,BSD,BSG,BSH,BSL,BSP,BSQ,BSR,BT1,BT6,BTB,BTC,BTD,BTG,BTH,BTN,BTR,BTU,BTV,BUD,BVB,BVG,BVL,BVN,BWA,BWS,BXD,BXT,C12,C21,C22,C36,C4G,C71,CAB,CAD,CAM,CAT,CBC,CBI,CBS,CC1,CC4,CCA,CCH,CCM,CCP,CCR,CCT,CCV,CDG,CDH,CDO,CDP,CDR,CE1,CEC,CEG,CEN,CER,CFC,CFM,CFV,CGL,CGP,CGV,CH5,CHC,CHS,CI5,CID,CIP,CK8,CKA,CKD,CKH,CLG,CLS,CLX,CMD,CMF,CMI,CMK,CMN,CMP,CMT,CMW,CNC,CNH,CNN,CNT,CPA,CPH,CPI,CPW,CQN,CQT,CSI,CST,CT3,CT5,CT6,CTA,CTN,CTR,CTW,CVC,CVH,CXH,CYC,CZC,D26,DAC,DAP,DAR,DAS,DBH,DBM,DBW,DC1,DCD,DCF,DCG,DCH,DCI,DCR,DCS,DCT,DDH,DDM,DDN,DDV,DFC,DFF,DGT,DHB,DHD,DHN,DIC,DID,DKC,DKH,DKP,DLC,DLD,DLR,DLT,DLV,DM7,DNA,DNB,DND,DNE,DNF,DNH,DNL,DNN,DNR,DNS,DNT,DNW,DNY,DOC,DOP,DP1,DP2,DPD,DPH,DPP,DPS,DRG,DRI,DSC,DSG,DSP,DSS,DSV,DT4,DTB,DTC,DTE,DTG,DTI,DTN,DTP,DTV,DUS,DVC,DVH,DVN,DVW,DWS,DX2,DXD,DXL,E12,E29,EAD,EFI,EIC,EIN,EME,EMG,EMS,EPC,EPH,EVF,FBA,FBC,FCC,FCS,FDG,FGL,FHN,FHS,FIC,FOC,FOX,FRC,FRM,FSC,FSO,FT1,FTI,G20,G36,GCB,GE2,GER,GGG,GGS,GH3,GHC,GLC,GLW,GND,GQN,GSM,GTC,GTD,GTH,GTK,GTS,GTT,GVT,H11,HAB,HAC,HAF,HAM,HAN,HAV,HAW,HBD,HBH,HBI,HBW,HC1,HC3,HCB,HCI,HCS,HD2,HD3,HD6,HD8,HDM,HDO,HDP,HDW,HEC,HEJ,HEM,HEP,HES,HFB,HFC,HFS,HFT,HFX,HGA,HGC,HGR,HGT,HGW,HHA,HHN,HHR,HHV,HIG,HIZ,HJC,HKB,HKC,HKP,HLA,HLB,HLE,HLG,HLR,HLS,HLT,HLY,HMG,HMS,HNA,HNB,HND,HNE,HNF,HNI,HNP,HNR,HNT,HPB,HPD,HPH,HPI,HPL,HPP,HPT,HPU,HPW,HRB,HRG,HRT,HSA,HSI,HSM,HSP,HSV,HTE,HTG,HTH,HTK,HTM,HTR,HTT,HTU,HTW,HU4,HU6,HUG,HUX,HVA,HVC,HVG,HWS,I10,IBD,IBN,ICC,ICF,ICI,ICN,IDN,IDP,IFC,IFS,IHK,IKH,ILA,ILC,ILS,IME,IMT,IN4,IPA,IPH,IRC,ISG,ISH,IST,ITS,JOS,JSC,KAC,KBE,KCB,KCE,KDF,KGM,KGU,KHA,KHB,KHD,KHL,KHW,KIP,KLB,KLM,KSA,KSC,KSE,KSH,KSK,KSS,KSV,KTB,KTC,KTL,KTU,L12,L44,L45,L63,LAI,LAW,LBC,LCC,LCW,LDW,LG9,LGM,LIC,LKW,LLM,LM3,LMC,LMH,LMI,LNC,LO5,LPT,LQN,LTC,LTG,LWS,LYF,M10,MA1,MBN,MC3,MCH,MCI,MCK,MCM,MCT,MDA,MDF,MDN,MDT,MEC,MEF,MEG,MES,MFS,MGC,MGG,MH3,MHP,MHY,MIC,MIE,MJC,MKP,MKT,MLC,MLN,MLS,MMC,MML,MNB,MND,MPC,MPT,MPY,MQB,MQN,MRF,MSR,MTA,MTB,MTC,MTG,MTH,MTL,MTM,MTP,MTS,MTV,MVC,MVN,MVY,MXC,NAB,NAC,NAS,NAU,NAW,NBE,NBR,NBS,NBT,NCP,NCS,ND2,NDC,NDF,NDP,NDT,NDW,NED,NGC,NHN,NHP,NHT,NHV,NJC,NLS,NMK,NNB,NNG,NNQ,NNT,NOS,NPH,NPS,NQB,NQN,NQT,NS2,NS3,NSG,NSL,NSP,NSS,NTB,NTC,NTF,NTR,NTT,NTW,NUE,NVP,NWT,OIL,OLC,ONW,ORS,PAI,PAP,PAS,PBC,PBK,PBT,PCC,PCF,PCM,PCN,PDT,PDV,PEC,PEG,PEQ,PFL,PFV,PGB,PGV,PHH,PHS,PID,PIS,PIV,PJS,PKR,PLA,PLE,PLO,PMJ,PMT,PMW,PND,PNG,PNP,PNT,POB,POS,POV,PPG,PPH,PPI,PQN,PRO,PRT,PSB,PSG,PSL,PSN,PSP,PTE,PTG,PTH,PTK,PTM,PTO,PTP,PTT,PTV,PTX,PVA,PVE,PVH,PVM,PVO,PVP,PVR,PVV,PVX,PVY,PWA,PWS,PX1,PXA,PXC,PXL,PXM,PXT,PYU,QBR,QCC,QHW,QLD,QLT,QNC,QNS,QNT,QNU,QNW,QPH,QSP,QTP,RAT,RBC,RCC,RCD,REM,RGC,RHN,RLC,RTB,RTH,RTS,S12,S27,S33,S72,S74,S96,SAC,SAL,SAP,SAS,SB1,SBD,SBH,SBL,SBM,SBR,SBS,SCA,SCC,SCG,SCH,SCJ,SCL,SCO,SCV,SCY,SD1,SD3,SD7,SD8,SDB,SDD,SDE,SDF,SDH,SDI,SDJ,SDK,SDP,SDV,SDX,SDY,SEA,SEP,SFT,SGB,SGO,SGP,SGS,SHC,SHG,SHV,SHX,SID,SIG,SIP,SIV,SJC,SJG,SJM,SKH,SKN,SKV,SLC,SNC,SNZ,SON,SOV,SP2,SPA,SPB,SPC,SPD,SPH,SPP,SPV,SQC,SRB,SRT,SSF,SSG,SSH,SSN,SST,SSU,STD,STH,STL,STS,STT,STU,STV,STW,SUM,SVG,SVH,SVL,SWC,SZE,T12,TA3,TA6,TAG,TAN,TAP,TAW,TB8,TBD,TBN,TBR,TBT,TCI,TCJ,TCK,TCW,TDA,TDB,TDF,TDS,TEC,TEL,TGP,TH1,THN,THP,THR,THU,THW,TID,TIE,TIS,TKA,TKG,TL4,TLI,TLP,TLT,TMG,TMW,TNB,TND,TNM,TNP,TNS,TNW,TNY,TOP,TOT,TOW,TPS,TQN,TQW,TR1,TRS,TRT,TS3,TS5,TSD,TSG,TSJ,TTD,TTG,TTJ,TTN,TTP,TTR,TTS,TTV,TUG,TV1,TV6,TVA,TVG,TVH,TVM,TVN,TVP,TVU,TVW,TW3,UCT,UDJ,UDL,UEM,UMC,UPC,UPH,USC,USD,V11,V15,VAB,VAV,VBB,VBG,VBH,VC5,VCE,VCGPVF,VCP,VCR,VCT,VCW,VCX,VDB,VDM,VDN,VDT,VE9,VEA,VEC,VECX,VEE,VEF,VES,VET,VFC,VFR,VFS,VGG,VGI,VGL,VGR,VGT,VGV,VHD,VHF,VHG,VHH,VHI,VIA,VIH,VIM,VIN,VIR,VIW,VKD,VKP,VLB,VLC,VLF,VLG,VLP,VLW,VMA,VMG,VMI,VNA,VNB,VNH,VNI,VNN,VNP,VNX,VNY,VOC,VPA,VPC,VPK,VPR,VPW,VQC,VRG,VSE,VSF,VSG,VSN,VSP,VST,VT1,VT8,VTA,VTD,VTE,VTG,VTI,VTK,VTM,VTP,VTQ,VTR,VTS,VTX,VVN,VW3,VWS,VXP,VXT,WSB,WTC,WTN,X18,X26,X77,XDH,XHC,XLV,XMC,XMD,XPH,YBC,YRC,YTC";
//        String listCodeToCheck = "PVC";
        String[] listSymbolArray = listCodeToCheck.split(",");
//        List<String> listSymbol = fileConverToObject.getListStringFromFile("./hnx.json");
//        List<String> listSymbol = fileConverToObject.getListStringFromFile("D:\\minhut\\java\\bot-stock\\src\\main\\resources\\hnx.json");
//        ArrayList<String> listSymbol = ArrayList.asList(listSymbolArray);
        ArrayList<String> listSymbol = new ArrayList<String>();
        for (String text : listSymbolArray) {
            listSymbol.add(text);
        }
//        System.out.println(String.join(",", listSymbol));
//        if (args.length > 0) {
//
//        }
        if (args.length > 0) {
            try (InputStream input = new FileInputStream(args[0])) {

                Properties prop = new Properties();

                // load a properties file
                prop.load(input);
                System.out.println(prop.getProperty("threshHold"));
                System.out.println(prop.getProperty("volDefault"));
                System.out.println(prop.getProperty("threadnums"));
//                System.out.println(prop.getProperty());
//                System.out.println(prop.getProperty());
                // get the property value and print it out
//                System.outprop.getProperty("db.url"));
//                System.out.println(prop.getProperty("db.user"));
//                System.out.println(prop.getProperty("db.password"));
                String listCode = prop.getProperty("listSymbol");
                if (listCode != null && !listCode.equalsIgnoreCase("A")) {
                    listSymbol.clear();
                    String[] listStringCode = listCode.split(",");
                    for (String code : listStringCode) {
                        listSymbol.add(code);
                    }
                }
                String threshHoldConfig = prop.getProperty("threshHold");
                if (threshHoldConfig != null) {
                    threshHold = Integer.valueOf(threshHoldConfig);
                }
                String volDefaultConfig = prop.getProperty("volDefault");
                String dayLastConfig = prop.getProperty("dayLast");
                String threadnums = prop.getProperty("threadnums");
                String responseSizeProp = prop.getProperty("responseSize");
                outputPath = prop.getProperty("outputPath") != null ? prop.getProperty("outputPath") : "minhut.xlsx";

                if (volDefaultConfig != null) {
                    volDefault = Integer.valueOf(volDefaultConfig);
                }
                if (dayLastConfig != null) {
                    dayLast = Integer.valueOf(dayLastConfig);
                }
                if (threadnums != null) {
//                    outputPath = args[4];
                    corePoolSize = Integer.valueOf(threadnums);
                }
                if (responseSizeProp != null) {
//                    outputPath = args[4];
                    responseSize = Integer.valueOf(responseSizeProp);
                }


            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }


//        listSymbol.clear();
//        listSymbol.add("PTB");
//        List<DataFrame> result = new ArrayList<DataFrame>();
        ConcurrentHashMap<String, Info> listCode = new ConcurrentHashMap<String, Info>();
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();

        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(maxPoolSize);

        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(160, 160 + 10, keepAlive, TimeUnit.MILLISECONDS, workQueue, handler);

        for (String code : listSymbol) {
            Runnable runnable = new CheckCodeRunable(listCode, code, threshHold, volDefault, dayLast, responseSize);
            threadPoolExecutor.execute(runnable);
        }
        int counter = 0;
        while (threadPoolExecutor.getActiveCount() > 0) {
            try {
//                System.out.println(threadPoolExecutor.getActiveCount());
                if (threadPoolExecutor.getActiveCount() < corePoolSize) {
                    counter++;
                }
                if (counter == 60) {
                    break;
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadPoolExecutor.shutdown();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Result");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        Row rowhead = sheet.createRow((short) 0);
        rowhead.createCell(0).setCellValue("Day");
        rowhead.createCell(1).setCellValue("Code");
        rowhead.createCell(2).setCellValue("PF");
        rowhead.createCell(3).setCellValue("VolRate");
        rowhead.createCell(4).setCellValue("PointBigMoney");
        rowhead.createCell(5).setCellValue("BigMoney");
        rowhead.createCell(6).setCellValue("CurrentPrice");
        rowhead.createCell(7).setCellValue("MaxPointBigMoney");
        rowhead.createCell(8).setCellValue("MaxPointOfDay");
        int i = 1;
        for (Map.Entry<String, Info> entry : listCode.entrySet()) {
            int start = i;
            int end = i + entry.getValue().getListDayVol().size() - 1;
            for (Map.Entry dayValue : entry.getValue().getListDayVol().entrySet()) {
                Row newRow = sheet.createRow(i);
                i++;
                newRow.createCell(0).setCellValue(dayValue.getKey().toString());
                newRow.createCell(1).setCellValue(entry.getValue().getCode());
                newRow.createCell(2).setCellValue(entry.getValue().getPf());
                newRow.createCell(3).setCellValue(dayValue.getValue().toString());
                newRow.createCell(4).setCellValue(entry.getValue().getPointBigMoney());
                newRow.createCell(5).setCellValue(entry.getValue().getListBigMoney());
                newRow.createCell(6).setCellValue(entry.getValue().getCurrentPrice());
                newRow.createCell(7).setCellValue(entry.getValue().getMaxBigPoint());
                newRow.createCell(8).setCellValue(entry.getValue().getMaxPointOfDay());
//                newRow.createCell(1).setCellValue();
            }
//            if (start < end) {
//                sheet.addMergedRegion(new CellRangeAddress(start, end, 0, 0));
//                sheet.addMergedRegion(new CellRangeAddress(start, end, 1, 1));
//                sheet.addMergedRegion(new CellRangeAddress(start, end, 2, 2));
//            }
        }

        Sheet sheet2 = workbook.createSheet("PointMax");
        sheet2.setColumnWidth(0, 6000);
        sheet2.setColumnWidth(1, 4000);
        Row rowhead2 = sheet2.createRow((short) 0);
        rowhead2.createCell(0).setCellValue("Code");
        rowhead2.createCell(1).setCellValue("PointBigMoney");
        rowhead2.createCell(2).setCellValue("BigMoney");
        rowhead2.createCell(3).setCellValue("CurrentPrice");
        rowhead2.createCell(4).setCellValue("MaxPointBigMoney");
        rowhead2.createCell(5).setCellValue("MaxPointOfDay");
        int j = 1;
        for (Info entry : listCode.values()) {
            if (entry.getMaxPointOfDay() > 0) {
                Row newRow = sheet2.createRow(j);
                j++;
                newRow.createCell(0).setCellValue(entry.getCode());
                newRow.createCell(1).setCellValue(entry.getPointBigMoney());
                newRow.createCell(2).setCellValue(entry.getListBigMoney());
                newRow.createCell(3).setCellValue(entry.getCurrentPrice());
                newRow.createCell(4).setCellValue(entry.getMaxBigPoint());
                newRow.createCell(5).setCellValue(entry.getMaxPointOfDay());
            }
        }

        Sheet sheet3 = workbook.createSheet("Minhut");
        sheet3.setColumnWidth(0, 6000);
        sheet3.setColumnWidth(1, 4000);
        Row rowhead3 = sheet2.createRow((short) 0);
        rowhead3.createCell(0).setCellValue("Day");
        rowhead3.createCell(1).setCellValue("Code");
        rowhead3.createCell(2).setCellValue("PF");
        rowhead3.createCell(3).setCellValue("VolRate");
        rowhead3.createCell(4).setCellValue("PointBigMoney");
        rowhead3.createCell(5).setCellValue("BigMoney");
        rowhead3.createCell(6).setCellValue("CurrentPrice");
        rowhead3.createCell(7).setCellValue("MaxPointBigMoney");
        rowhead3.createCell(8).setCellValue("MaxPointOfDay");
        int k = 1;
        for (Map.Entry<String, Info> entry : listCode.entrySet()) {
//            int start = k;
//            int end = k + entry.getValue().getListDayVol().size() - 1;
            if (entry.getValue().getListDayVol().size() == 4) {
                for (Map.Entry dayValue : entry.getValue().getListDayVol().entrySet()) {
                    Row newRow = sheet3.createRow(k);
                    k++;
                    newRow.createCell(0).setCellValue(dayValue.getKey().toString());
                    newRow.createCell(1).setCellValue(entry.getValue().getCode());
                    newRow.createCell(2).setCellValue(entry.getValue().getPf());
                    newRow.createCell(3).setCellValue(dayValue.getValue().toString());
                    newRow.createCell(4).setCellValue(entry.getValue().getPointBigMoney());
                    newRow.createCell(5).setCellValue(entry.getValue().getListBigMoney());
                    newRow.createCell(6).setCellValue(entry.getValue().getCurrentPrice());
                    newRow.createCell(7).setCellValue(entry.getValue().getMaxBigPoint());
                    newRow.createCell(8).setCellValue(entry.getValue().getMaxPointOfDay());
//                newRow.createCell(1).setCellValue();
                }
            }
//            if (start < end) {
//                sheet.addMergedRegion(new CellRangeAddress(start, end, 0, 0));
//                sheet.addMergedRegion(new CellRangeAddress(start, end, 1, 1));
//                sheet.addMergedRegion(new CellRangeAddress(start, end, 2, 2));
//            }
        }

        System.out.println(listCode);
        try {
//            TelegramNotifier.sendMessage(listCode.toString());
            FileOutputStream fileOut = new FileOutputStream(outputPath);
            workbook.write(fileOut);
//closing the Stream
            fileOut.close();
//closing the workbook
            workbook.close();
        } catch (Exception e) {

        }
        System.out.println("end");


    }

    private static void runCheckCode(Map<String, String> key, Long nowTime, Long fromTime, int threshHold, int volDefault, int dayLast, ConcurrentHashMap<String, String> listCode, String code) throws UnsupportedEncodingException {
        key.put("ticker", code);
        String request = ParameterStringBuilder.getParamsString(key);
        int counter = 0;
        try {
            StringBuffer response = callRequest(request);
            double currentPrice = getCurrentPrice(response);
            double recommendPrice = 0d;
            for (int i = 2000; i > -1; i--) {
                DataFrame s = checkRequestBuy(response, i, threshHold);
//                    DataFrame s = checkRequestSell(response, i);

                if (s != null) {
//                        result.add(s);
//                        LOGGER.info(s.toString());
//                        listCode.add(s.getSymbol());
                    counter++;
//                        recommendPrice = s.getClose();
//                        System.out.println(s.toString(""));
                }
            }
            if (counter > 0) {
//                    listCode.put(code, currentPrice * 100 / recommendPrice);
                Map<String, String> keyDay = new HashMap<String, String>();
                keyDay.put("type", "stock");
                keyDay.put("ticker", code);
//                    key.put("resolution", "5");
//                    Long nowTime = new Date().getTime() / 1000;
//                    Long fromTime = nowTime - 60 * 60 * 24 * 200;
                keyDay.put("from", fromTime.toString());
                keyDay.put("to", nowTime.toString());
                keyDay.put("resolution", "D");
                String requestDay = ParameterStringBuilder.getParamsString(keyDay);
                StringBuffer responseDay = callRequestDay(requestDay);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < dayLast; i++) {
//                    DataFrame s = checkRequestBuyDay(responseDay, i, volDefault, info);
//                    if (s != null) {
//                        stringBuilder.append("Ngay: " + s.getTradingDate() + " Khoi luong so voi Trung binh " + volDefault + " phien= " + s.getPoint() * 100 + "%;");
//                    }
                }
                listCode.put(code, stringBuilder.toString());
            }
        } catch (Exception e) {
            System.out.println("error: " + code + e.getMessage());
        }
    }

    public static double getCurrentPrice(StringBuffer response) {
        if (response == null) {
            return -1d;
        } else {
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            DataFrame lastData = responseTcbs.getData().get(responseTcbs.getData().size() - 1);
            return lastData.getClose();
        }
    }
}
