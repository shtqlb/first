package com.example.db;

import org.json.JSONObject;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class DBAgentAbutmentUtil {
	
	
    private static final String SESSION_TOKEN_PARAM = "anti_csrf_token";
    private static final String API_DATA_PARAM = "__hh_data";
    private static String apiSessionIdProp = null;
    private static String sess_csrf_token = null;
    private static String loginUrl = "https://oa.deskui.com/apilogin";
    private static String doApiUrl = "https://oa.deskui.com//api";
    private static String userName = "tapi";
    private static String pass = "Api#2o22";

    public static void getTocken() throws Exception {
        //查看系统参数PASS_USE_SHA=true则DigestUtils.sha1Hex(pass)，否则使用md5
        //加密
        String loginParam = "user=" + URLEncoder.encode(userName, "UTF-8")
                + "&pass=" + DigestUtils.md5DigestAsHex(pass.getBytes(StandardCharsets.UTF_8));
        HttpURLConnection httpUrlConn = getHttpConn(loginUrl);
        // 设置是否从 httpUrlConnection 读入，默认情况下是 true;
        httpUrlConn.setDoInput(true);
        // 设置是否向 httpUrlConnection 输出
        httpUrlConn.setDoOutput(true);
        // Post 请求不能使用缓存
        httpUrlConn.setUseCaches(false);
        // 设定请求的方法，默认是 GET
        httpUrlConn.setRequestMethod("POST");
        //使用标准编码格式编码参数的名-值对
        httpUrlConn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");
        if (!StringUtils.isEmpty(loginParam)) {
            OutputStream outputStream = httpUrlConn.getOutputStream();
            try {
                outputStream.write(loginParam.getBytes(StandardCharsets.UTF_8));
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        }
        String resultString = getResultByConn(httpUrlConn);
        JSONObject loginResult = new JSONObject(resultString);
        System.out.println(loginResult.toString(4));
        if (loginResult.getInt("code") != 0) {
            System.out.println("登陆失败了：" + loginResult.getString("msg"));
        } else {
            //成功则记录 anti_csrf_token 参数值
            sess_csrf_token = loginResult.getString(SESSION_TOKEN_PARAM);
        }
    }

    public static JSONObject getAllDBResouceByAccount() throws Exception{
        System.out.println("---------------------------------------------用户登录【tapi】---------------------------------------------");
        //先登录，注意连接获取时设置 sessionId。以保持 session
        getTocken();
        String toDoApiUrl=doApiUrl+"?"+SESSION_TOKEN_PARAM+"="+sess_csrf_token;
        System.out.println("请求："+toDoApiUrl);
        //获取数据库资源
        //获取用户 tmgr 创建的所有数据库资源
        System.out.println("---------------------------------------------获取数据库资源【tmgr】---------------------------------------------");
        JSONObject findParam = new JSONObject();
        findParam.put("api_type", "FIND_DB_RES");
        findParam.put("owner_user_name", "tope");//资源所属用户登录名
//        System.out.println("获取用户 tmgr 的所有数据库资源：");
//        doApi(toDoApiUrl, findParam);
        System.out.println("条件查询用户 tmgr 的数据库资源：");
        //findParam.put("db_type", "mysql");
        return doApi(toDoApiUrl, findParam);
    }

    public static JSONObject assignDBResource() throws Exception {
        System.out.println("---------------------------------------------用户登录【tapi】---------------------------------------------");
        //先登录，注意连接获取时设置 sessionId。以保持 session
        getTocken();
        String toDoApiUrl=doApiUrl+"?"+SESSION_TOKEN_PARAM+"="+sess_csrf_token;
        System.out.println("请求："+toDoApiUrl);
        //数据库资源授权
        System.out.println("---------------------------------------------数据库资源授权【tope】---------------------------------------------");
        System.out.println("数据库资源授权给用户 tope：");
        JSONObject granParam = new JSONObject();
        granParam.put("api_type", "GRANT_DB_RES");
        granParam.put("target_user_name", "tope");
        granParam.put("db_res_id", 34);
        return doApi(toDoApiUrl, granParam);
    }
    private static JSONObject doApi(String toDoApiUrl,JSONObject jsonData) throws
            Exception {
        //url 添加 token 参数 anti_csrf_token
        String postParamString=jsonData.toString();//参数为 json 字符串
        HttpURLConnection httpUrlConn = getHttpConn(toDoApiUrl);
        // 设定请求的方法，默认是 GET
        httpUrlConn.setRequestMethod("POST");
        // 设置是否从 httpUrlConnection 读入，默认情况下是 true;
        httpUrlConn.setDoInput(true);
        // 设置是否向 httpUrlConnection 输出
        httpUrlConn.setDoOutput(true);
        // Post 请求不能使用缓存
        httpUrlConn.setUseCaches(false);
        httpUrlConn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
        if(!StringUtils.isEmpty(postParamString)) {
            OutputStream outputStream = httpUrlConn.getOutputStream();
            try {
                outputStream.write(postParamString.getBytes(StandardCharsets.UTF_8));
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        }
        //获取结果
        String resultString= getResultByConn(httpUrlConn);
        JSONObject resultObject = new JSONObject(resultString);
        System.out.println("===================请求参数如下： ===================");
        System.out.println(jsonData.toString(4));
        System.out.println("===================请求结果如下： ===================");

        System.out.println(resultObject.toString(4));
        if(resultObject.getInt("code")!=0) {
            System.out.println("出错了："+resultObject.getString("msg"));
        }
        return resultObject;
    }

    /**
     * 获取连接 HttpURLConnection
     *
     * @param requestUrl
     * @return
     * @throws Exception
     */
    private static HttpURLConnection getHttpConn(String requestUrl) throws
            Exception {
        URL url = new URL(requestUrl);
        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
        //必须设置 XMLHttpReques
        httpUrlConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        //获取保存的用户第一次请求后获取 session 属性，设置 sessionId，以保持session 一致
        if (apiSessionIdProp != null) {
            httpUrlConn.setRequestProperty("Cookie", apiSessionIdProp);
        }
        return httpUrlConn;
    }

    /**
     * 获取请求返回的结果
     *
     * @param httpUrlConn
     * @return
     * @throws Exception
     */
    private static String getResultByConn(HttpURLConnection httpUrlConn) throws
            Exception {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder buffer = new StringBuilder();
        try {
            inputStream = httpUrlConn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            if (apiSessionIdProp == null) {
                //用户第一次请求后记录 session 属性
                String cookieVal = httpUrlConn.getHeaderField("Set-Cookie");
                if (cookieVal != null) {
                    String apiServiceSessionId = cookieVal.substring(0,
                            cookieVal.indexOf(";"));
                    apiSessionIdProp = apiServiceSessionId;
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }
        return buffer.toString();
    }
}
