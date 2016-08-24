package com.example.god.myapplication;





        import java.io.IOException;
        import java.util.Dictionary;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Map;

        import org.ksoap2.SoapEnvelope;
        import org.ksoap2.SoapFault;
        import org.ksoap2.serialization.SoapObject;
        import org.ksoap2.serialization.SoapPrimitive;
        import org.ksoap2.serialization.SoapSerializationEnvelope;
        import org.ksoap2.transport.HttpResponseException;
        import org.ksoap2.transport.HttpTransportSE;
        import org.xmlpull.v1.XmlPullParserException;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.TextView;
        import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
    TextView tvMessage;
//    package com.bang.testwebservicecall;
    final String METHOD_HELLO_WORLD = "HelloWorld";
    final String METHOD_ECHO_MESSAGE = "EchoMessage";
    //服务器链接
    final String WEB_SERVICE_URL = "http://192.168.1.101:8101/WebService1.asmx";
    final String Namespace = "http://tempuri.org/";//命名空间
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBtn();// 初始化按钮
        initTv();
    }

    private void initTv() {
        tvMessage = (TextView) this.findViewById(R.id.tvMessage);
    }

    private void initBtn() {
        View btnHelloWorld = this.findViewById(R.id.btnHelloWorld);
        btnHelloWorld.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Map<String, String> values = new HashMap<String, String>();
                values.put("msg", "这是Android手机发出的信息");
                Request(METHOD_HELLO_WORLD);
            }
        });

        View btnEchoMessage = this.findViewById(R.id.btnEchoMessage);
        btnEchoMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> values = new HashMap<String, String>();
                values.put("msg", "这是Android手机发出的信息");
                Request(METHOD_ECHO_MESSAGE, values);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }

    /**
     * 调用WebService
     *
     * @return WebService的返回值
     *
     */
    public String CallWebService(String MethodName, Map<String, String> Params) {
        // 1、指定webservice的命名空间和调用的方法名

        SoapObject request = new SoapObject(Namespace, MethodName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                request.addProperty((String) entry.getKey(),
                        (String) entry.getValue());
            }
        }
        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);
        envelope.bodyOut = request;
        // c#写的应用程序必须加上这句
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(WEB_SERVICE_URL);
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            final SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            if (result != null) {
                Log.d("----收到的回复----", result.toString());
                return result.toString();
            }

        } catch (SoapFault e) {
            Log.e("----发生错误---", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行异步任务
     *
     * @param params
     *            方法名+参数列表（哈希表形式）
     */
    public void Request(Object... params) {
        new AsyncTask<Object, Object, String>() {

            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    return CallWebService((String) params[0],
                            (Map<String, String>) params[1]);
                } else if (params != null && params.length == 1) {
                    return CallWebService((String) params[0], null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    tvMessage.setText("服务器回复的信息 : " + result);
                }
            };

        }.execute(params);
    }

}
