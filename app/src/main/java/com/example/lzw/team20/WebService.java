package com.example.lzw.team20;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.example.lzw.team20.activity.LoginActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import static java.net.Proxy.Type.HTTP;

/**
 * Created by Myh on 2018/4/20.
 */

public class WebService {

    private static String IP = "112.74.177.29:8080/together";
    public static final String COOKIE="cookie" ;
    public static final String ISLOGINED = "islogined";
    private static String cookie;


    /**
     * 获取验证码
     *
     * @param telStr 手机号
     * @return 详情:(Boolean)result,(String)message,(T)data=null
     */
    // 通过Get方式获取HTTP服务器数据
    public static String executeSendShortMessage(String telStr) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/user/sendShortMessage";
            path = path + "?phone=" + URLEncoder.encode(telStr, "utf-8");
            Log.e("webServiceTag", "executeSendShortMessage: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 注册用户
     *
     * @param name 用户名
     * @param password 密码
     * @param phone 手机号
     * @param vericode 验证码
     * @param choose 头像代码
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String executeRegister(String name,String password,String phone,String vericode,int choose) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/user/register";
            path = path + "?username=" + URLEncoder.encode(name, "utf-8") + "&pwd=" + password+"&phone="+phone+"&code="+vericode+"&photo="+choose;
            Log.e("webServiceTag", "executeRegister: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 找回密码
     *
     * @param phone 手机号
     * @param newPwd 新密码
     * @param vericode 验证码
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String executeRetrieve(String phone,String newPwd,String vericode) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/user/resetPwd";
            path = path + "?phone=" + phone + "&newPwd=" + newPwd+"&code="+vericode;
            Log.e("webServiceTag", "executeRegister: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 登录
     *
     * @param name 用户名
     * @param password 密码
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String executeLogin(String name,String password) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/user/signin";
            path = path + "?username=" + URLEncoder.encode(name, "utf-8") + "&pwd=" + password;
            Log.e("webServiceTag", "executeLogin: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            String cookieval=conn.getHeaderField("set-cookie");
            if (cookieval!=null){
                cookie=cookieval.substring(0,cookieval.indexOf(";"));
                setCookie(cookie);
            }
            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 修改个人信息
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String executeOut() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/user/signout";
            Log.e("webServiceTag", "executeOut: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            conn.setRequestProperty("Cookie", getCookie());
            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 修改个人信息
     *
     * @param name 用户名
     * @param school 学校
     * @param hobby 爱好
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String executeModify(String name,String school,String hobby) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/user/modify";
            path = path + "?username=" + URLEncoder.encode(name, "utf-8") + "&school=" + URLEncoder.encode(school, "utf-8")+"&hobby="+URLEncoder.encode(hobby, "utf-8");
            Log.e("webServiceTag", "executeLogin: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 根据userid查找某一用户
     * @param userid 搜索关键词
     *
     * @return JSONObject
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getUser(String userid) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/user/getUser";
            path = path + "?userid=" + URLEncoder.encode(userid, "utf-8");
            Log.e("webServiceTag", "getRtables: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 创建需求
     *
     * @param createUserID 创建者ID
     * @param maxCount 单人或多人（待确认）
     * @param detail 需求介绍
     * @param first_tag 一级标签
     * @param second_tag 二级标签
     * @param school 学校
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String createNeed(String createUserID,String first_tag,String second_tag,String detail,String maxCount,String school) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/need/release";
            path = path + "?createUserID=" + URLEncoder.encode(createUserID, "utf-8") + "&firstTag=" + URLEncoder.encode(first_tag, "utf-8")+"&secondTag="+URLEncoder.encode(second_tag, "utf-8")+"&detail="+URLEncoder.encode(detail, "utf-8")+"&maxcount="+URLEncoder.encode(maxCount, "utf-8")+"&school="+URLEncoder.encode(school, "utf-8");
            Log.e("webServiceTag", "createNeed: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 创建草稿
     *
     * @param createUserID 创建者ID
     * @param maxCount 单人或多人（待确认）
     * @param detail 需求介绍
     * @param first_tag 一级标签
     * @param second_tag 二级标签
     * @param school 学校
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String createDraft(String createUserID,String first_tag,String second_tag,String detail,String maxCount,String school) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/need/releaseUndo";
            path = path + "?createUserID=" + URLEncoder.encode(createUserID, "utf-8") + "&firstTag=" + URLEncoder.encode(first_tag, "utf-8")+"&secondTag="+URLEncoder.encode(second_tag, "utf-8")+"&detail="+URLEncoder.encode(detail, "utf-8")+"&maxcount="+URLEncoder.encode(maxCount, "utf-8")+"&school"+URLEncoder.encode(school, "utf-8");
            Log.e("webServiceTag", "createDraft: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 创建圆桌
     *
     * @param needID 需求ID
     * @param RTablename 圆桌名称（可不传，则用二级标签代替）
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String createRTable(String needID,String RTablename,String ddl,String max,String uphoto) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/create";
            path = path + "?needid=" + URLEncoder.encode(needID, "utf-8") + "&rtablename=" + URLEncoder.encode(RTablename, "utf-8") + "&ddl=" + URLEncoder.encode(ddl, "utf-8") + "&maxNumber=" + URLEncoder.encode(max, "utf-8")+"&photoUrl=" + URLEncoder.encode(uphoto, "utf-8");
            Log.e("webServiceTag", "createRTable: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 获取全部圆桌（按截止时间排序）
     *
     * @return Page<RTable>
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getSchoolRtablesByDDL() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/getSchoolRtablesByDDL";
            Log.e("webServiceTag", "getSchoolRtablesByDDL: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 获取全部圆桌（按截止时间排序）
     *
     * @return Page<RTable>
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getSchoolRtablesByCT() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/getSchoolRtablesByCT";
            Log.e("webServiceTag", "getSchoolRtablesByCT: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 模糊搜索圆桌（按截止时间排序）
     * @param keyWord 搜索关键词
     *
     * @return Page<RTable>
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getRtables(String keyWord) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/searchRtable";
            path = path + "?keyWord=" + URLEncoder.encode(keyWord, "utf-8");
            Log.e("webServiceTag", "getRtables: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 根据tableid精确查找圆桌
     * @param rtableid 搜索关键词
     *
     * @return JSONObject
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getOne(String rtableid) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/getOne";
            path = path + "?rtableid=" + URLEncoder.encode(rtableid, "utf-8");
            Log.e("webServiceTag", "getRtables: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 判断是否已加入圆桌
     * @param rtableid 搜索关键词
     *
     * @return Boolean
     */
    // 通过Get方式获取HTTP服务器数据
    public static String isJoined(String rtableid) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/isJoined";
            path = path + "?rtableid=" + URLEncoder.encode(rtableid, "utf-8");
            Log.e("webServiceTag", "getRtables: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 加入圆桌
     *
     * @param rtableid 圆桌id
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String joinRTable(String rtableid) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/join";
            path = path + "?rtableid=" + URLEncoder.encode(rtableid, "utf-8");
            Log.e("webServiceTag", "createRTable: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 退出圆周
     *
     * @param rtableid 圆桌id
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String exitRTable(String rtableid) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/exitRtable";
            path = path + "?rtableid=" + URLEncoder.encode(rtableid, "utf-8");
            Log.e("webServiceTag", "createRTable: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 获取当前圆桌所有成员信息
     * @param rtableid 搜索关键词
     *
     * @return JSONObject
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getMembers(String rtableid) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/getMembers";
            path = path + "?rtableid=" + URLEncoder.encode(rtableid, "utf-8");
            Log.e("webServiceTag", "getRtables: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 获取本人全部草稿
     *
     * @return List<Need>
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getAllUndo() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/need/getAllUndo";
            Log.e("webServiceTag", "getAllUndo: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 获取本人创建的全部圆桌
     *
     * @return List<RTable>
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getUserCreateAll() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/getUserCreateAll";
            Log.e("webServiceTag", "getUserCreateAll: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 获取本人加入的全部圆桌
     *
     * @return List<RTable>
     */
    // 通过Get方式获取HTTP服务器数据
    public static String getUserAttendAll() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/rtable/getUserAttendAll";
            Log.e("webServiceTag", "getUserAttendAll: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            is=conn.getInputStream();
            return parseInfo(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 修改草稿
     *
     * @param needid 创建者ID
     * @param maxCount 单人或多人（待确认）
     * @param detail 需求介绍
     * @param first_tag 一级标签
     * @param second_tag 二级标签
     * @param school 学校
     * @return 详情:(Boolean)result,(String)message,(T)data
     */
    // 通过Get方式获取HTTP服务器数据
    public static String changeDraft(String needid,String first_tag,String second_tag,String detail,String maxCount,String school) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + IP + "/need/changeUndo";
            path = path + "?createUserID=" + URLEncoder.encode(needid, "utf-8") + "&firstTag=" + URLEncoder.encode(first_tag, "utf-8")+"&secondTag="+URLEncoder.encode(second_tag, "utf-8")+"&detail="+URLEncoder.encode(detail, "utf-8")+"&maxcount="+URLEncoder.encode(maxCount, "utf-8")+"&school"+URLEncoder.encode(school, "utf-8");
            Log.e("webServiceTag", "createDraft: "+path);
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(6000); // 设置超时时间
            conn.setReadTimeout(6000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式


            conn.setRequestProperty("Cookie", getCookie());

            int code=conn.getResponseCode();
            if (code == 200) {//HTTP_OK
                is = conn.getInputStream();
                return parseInfo(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }






    public static void setCookie(String value) {
        cookie=value;
    }

    public static String getCookie() {
        return cookie;
    }

    // 将输入流转化为 String 型
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }

    // 将输入流转化为byte型
    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }


}
