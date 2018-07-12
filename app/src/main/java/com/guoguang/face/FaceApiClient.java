package com.guoguang.face;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.FaceVerifyRequest;
import com.baidu.aip.face.MatchRequest;
import com.baidu.aip.util.Base64Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wangjr on 2018/7/11.
 */

public class FaceApiClient {

    public static final String TAG = "FaceApiClient";
    //百度人脸识别应用id
    public static final String APP_ID = "11517427";
    //百度人脸识别应用apikey
    public static final String API_KEY = "HaZQuIsCXNuvDMxuswBGUh8Y";
    //百度人脸识别应用sercetkey
    public static final String SERCET_KEY = "cAWf7FkBo2TG67ZxVRwlL9qTF5DsOhpG";

    static AipFace client = null;

    static {
        client = new AipFace(APP_ID, API_KEY, SERCET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }


    /**
     * 人脸检测
     *
     * @param image      图片信息
     * @param maxFaceNum 最大检测人脸数量
     * @return
     * @throws JSONException
     */
    public static JSONObject detectFace(byte[] image, String maxFaceNum) throws JSONException {

        // 传入可选参数调用接口
        //face_field 检测返回参数 max_face_num最多处理人脸数目 face_type 照片类型
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age,beauty,expression,faceshape,gender,glasses,race,qualities");
        options.put("max_face_num", maxFaceNum);
        options.put("face_type", "LIVE");

        String imageType = "BASE64";
        String imgStr = Base64Util.encode(image);
        //人脸检测
        JSONObject res = client.detect(imgStr, imageType, options);
        return res;
    }

    /**
     * 人脸搜索
     *
     * @param image       图片信息
     * @param userId      搜索的用户id（可选）
     * @param groupIdList 搜索的用户组
     * @return
     * @throws JSONException
     */
    public static JSONObject searchFace(byte[] image, String userId, String groupIdList) throws JSONException {

        // 传入可选参数调用接口
        //quality_control图片质量控制 liveness_control活体检测控制 max_user_num返回的用户数量，相似度最高的几个用户
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("max_user_num", "3");
        if (userId != null) {
            options.put("user_id", userId);
        }

        String imageType = "BASE64";
        String imgStr = Base64Util.encode(image);

        // 人脸搜索
        JSONObject res = client.search(imgStr, imageType, groupIdList, options);
        return res;
    }

    /**
     * 人脸注册
     *
     * @param image    图片信息
     * @param userInfo 用户资料
     * @param userId   用户id
     * @param groupId  用户组id
     * @return
     * @throws JSONException
     */
    public static JSONObject addUserFace(byte[] image, String userInfo, String userId, String groupId) throws JSONException {
        // 传入可选参数调用接口
        //user_info用户资料 quality_control图片质量控制 liveness_control活体检测控制
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_info", userInfo);
        options.put("quality_control", "LOW");
        options.put("liveness_control", "LOW");

        String imageType = "BASE64";
        String imgStr = Base64Util.encode(image);
        // 人脸注册
        JSONObject res = client.addUser(imgStr, imageType, groupId, userId, options);
        return res;
    }

    /**
     * 人脸更新
     *
     * @param image   图片信息
     * @param userId  用户id
     * @param groupId 用户组id
     * @return
     * @throws JSONException
     */
    public static JSONObject updateUserFace(byte[] image, String userId, String groupId) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_info", "user's info");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");

        String imageType = "BASE64";
        String imgStr = Base64Util.encode(image);

        // 人脸更新
        JSONObject res = client.updateUser(imgStr, imageType, groupId, userId, options);
        return res;
    }

    /**
     * 人脸删除
     *
     * @param userId    用户id
     * @param groupId   用户组id
     * @param faceToken face_token 唯一标识
     * @return
     * @throws JSONException
     */
    public static JSONObject deleteUserFace(String userId, String groupId, String faceToken) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        // 人脸删除
        JSONObject res = client.faceDelete(userId, groupId, faceToken, options);
        return res;
    }

    /**
     * 人脸比对
     *
     * @param image1
     * @param image2
     * @return
     * @throws JSONException
     */
    public static JSONObject matchFace(byte[] image1, byte[] image2) throws JSONException {
        String imgStr1 = Base64Util.encode(image1);
        String imgStr2 = Base64Util.encode(image2);

        // image1/image2也可以为url或facetoken, 相应的imageType参数需要与之对应。
        MatchRequest req1 = new MatchRequest(imgStr1, "BASE64");
        MatchRequest req2 = new MatchRequest(imgStr2, "BASE64");
        ArrayList<MatchRequest> requests = new ArrayList<MatchRequest>();
        requests.add(req1);
        requests.add(req2);

        //人脸比对
        JSONObject res = client.match(requests);
        return res;
    }

    /**
     * 活体检测
     *
     * @param image
     * @return
     * @throws JSONException
     */
    public static JSONObject faceVerify(byte[] image) throws JSONException {
        String imgStr = Base64Util.encode(image);
        FaceVerifyRequest req = new FaceVerifyRequest(imgStr, "BASE64");
        ArrayList<FaceVerifyRequest> list = new ArrayList<FaceVerifyRequest>();
        list.add(req);
        JSONObject res = client.faceverify(list);
        return res;
    }

    /**
     * 身份验证
     *
     * @param image        图片信息
     * @param idCardNumber 身份证号
     * @param name         姓名
     * @return
     * @throws JSONException
     */
    public static JSONObject personVerify(byte[] image, String idCardNumber, String name) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");

        //String image = "取决于image_type参数，传入BASE64字符串或URL字符串或FACE_TOKEN字符串";
        String imageType = "BASE64";
        String imgStr = Base64Util.encode(image);

        // 身份验证
        JSONObject res = client.personVerify(imgStr, imageType, idCardNumber, name, options);
        return res;
    }

    /**
     * 用户信息查询
     *
     * @param userId  用户id
     * @param groupId 用户组id
     * @return
     * @throws JSONException
     */
    public static JSONObject getUserInfo(String userId, String groupId) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        // 用户信息查询
        JSONObject res = client.getUser(userId, groupId, options);
        return res;
    }

    /**
     * 获取用户人脸列表
     *
     * @param userId  用户id
     * @param groupId 用户组id
     * @return
     * @throws JSONException
     */
    public static JSONObject getUserFaceList(String userId, String groupId) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        // 获取用户人脸列表
        JSONObject res = client.faceGetlist(userId, groupId, options);
        return res;
    }

    /**
     * 获取用户列表
     *
     * @param groupId 用户组id
     * @return
     * @throws JSONException
     */
    public static JSONObject getGroupUsers(String groupId) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("start", "0");
        options.put("length", "50");

        // 获取用户列表
        JSONObject res = client.getGroupUsers(groupId, options);
        return res;
    }

    /**
     * 复制用户
     *
     * @param userId 要复制的用户
     * @param group1 原来用户组
     * @param group2 目标用户组
     * @return
     * @throws JSONException
     */
    public static JSONObject copyUser(String userId, String group1, String group2) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("src_group_id", group1);
        options.put("dst_group_id", group2);

        // 复制用户
        JSONObject res = client.userCopy(userId, options);
        return res;
    }

    /**
     * 删除用户
     *
     * @param userId  用户id
     * @param groupId 用户组id
     * @return
     * @throws JSONException
     */
    public static JSONObject deleteUser(String userId, String groupId) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        // 删除用户
        JSONObject res = client.deleteUser(groupId, userId, options);
        return res;
    }

    /**
     * 创建用户组
     *
     * @param groupId 用户组id
     * @return
     */
    public static JSONObject addGroup(String groupId) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        // 创建用户组
        JSONObject res = client.groupAdd(groupId, options);
        return res;
    }

    /**
     * 删除用户组
     *
     * @param groupId 用户组id
     * @return
     */
    public static JSONObject deleteGroup(String groupId) throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        // 创建用户组
        JSONObject res = client.groupDelete(groupId, options);
        return res;
    }

    /**
     * 用户组列表查询
     *
     * @param
     * @return
     */
    public static JSONObject getGroupList() throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("start", "0");
        options.put("length", "50");

        // 组列表查询
        JSONObject res = client.getGroupList(options);
        return res;
    }


}
