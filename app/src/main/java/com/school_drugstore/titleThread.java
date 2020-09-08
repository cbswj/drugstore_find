package com.school_drugstore;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class titleThread extends Thread{


    //title부분을 저장하기 위한 객체 선언
    Vector<String> titlevec = new Vector<String>();
    //주소 저장하기 위한 객체 선언
    Vector<String> addressvec = new Vector<String>();
    //전화번호 저장하기 위한 객체 선언
    Vector<String> telvec = new Vector<String>();
    //latitudedvec를 저장하기 위한 객체 선언
    Vector<String> latitudevec = new Vector<String>();
    //longitudevec를 저장하기 위한 객체 선언
    Vector<String> longitudevec = new Vector<String>();


    XmlPullParser xpp=null;

    //웹사이트에 연결하기위해서 url 클래스를 적용
    URL url;

    //연결할 사이트 주소 선택
    StringBuffer uri;
    //xml에서 읽어드려서 저장할 변수
    String tagname=null,title=null,address=null,tel=null,latitude=null, longitude=null;
    //제대로 데이터가 읽어졌는지를 판단해주는 변수
    boolean flag=false;

    String x;
    String y;

    public titleThread(String x, String y){
        this.x=x;
        this.y=y;
    }



    @Override
    public void run() {
        super.run();
        uri=new StringBuffer("http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyLcinfoInqire?serviceKey=");
        uri.append("NMmnx5nmo4qnoubfxajz%2FLuJ95J%2Bcfwll%2BF%2BGXO74nk91CVZFHYFv94DHpbfOQzwyYhR6eZ8ldtKbWudPwed3g%3D%3D");  // 서버키
        uri.append("&WGS84_LAT=");
        uri.append(x);
        uri.append("&WGS84_LON="); // 컨텐츠 내용 구성
        uri.append(y);
        uri.append("&numOfRows=1000");

        //안드로이드에서 xml문서를 읽고 파싱하는 객체를 선언
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();

            //네임스페이스 사용여부
            factory.setNamespaceAware(true);

            //실제 sax형태로 데이터를 파싱하는 객체 선언
            xpp = factory.newPullParser();

            //웹사이트에 접속
            url = new URL(uri.toString());
            //웹사이트를 통해서 읽어드린 xml문서를 안드로이드에 저장
            InputStream in = url.openStream();
            //xml문서를 일고 파싱하는 객체에 넘겨줌
            xpp.setInput(in,"UTF-8"); //xml문서의 인코딩 정확히 지정


        } catch (XmlPullParserException e) {
            Log.d("PULL","pull e : ");
        } catch (MalformedURLException e) {
            Log.d("URL","url e : ");
        } catch (IOException e) {
            Log.d("IOE","input output e : ");
        }

        //item 태그를 안이라면
        boolean isInItemTag = false;

        //이벤트 타입을 얻어옴
        int eventType = 0;  // xpp 현재 파싱하고 있는 객체
        try {
            eventType = xpp.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        //문서의 끝까지 읽어 드리면서 title과 descripton을 추출해냄
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                //태그명을 읽어드림
                tagname = xpp.getName();

                if (tagname.equals("item")) {
                    isInItemTag = true;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                //태그명이 title이거나 또는 description일때 읽어옴
                if (tagname.equals("dutyName") && isInItemTag) {
                    title = xpp.getText(); //text에 해당하는 모든 텍스트를 읽어드림 ( += )
                } else if (tagname.equals("dutyAddr") && isInItemTag) {
                    address = xpp.getText();
                } else if (tagname.equals("dutyTel1") && isInItemTag) {
                    tel = xpp.getText();
                } else if (tagname.equals("latitude") && isInItemTag) {
                    latitude = xpp.getText();
                }else if (tagname.equals("longitude") && isInItemTag) {
                    longitude = xpp.getText();
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                //태그명을 읽어드림
                tagname = xpp.getName();

                //endtag일경우에만 벡터에 저장
                if (tagname.equals("item")) {
                    //벡터에 저장
                    titlevec.add(title);
                    addressvec.add(address);
                    telvec.add(tel);
                    latitudevec.add(latitude);
                    longitudevec.add(longitude);
                    isInItemTag = false;

                }//if-------
            }//if----------
            //다음 이벤트 다입을 저장
            try {
                eventType = xpp.next();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }//while---------
        //모든 데이터가 저장되었다면.
        flag = true; //true : 지정된 xml파일을 읽고 필요한 데이터를 추출해서 저장 완료된 상태
    }
}
