package com.quickbird.speedtestengine.storage.xml;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.quickbird.controls.Constants;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;

public class TecentTokenKeeper {
    private static final String PREFERENCES_NAME = "com_tencent_weibo_oathv2";
    /**
     * 保存accesstoken到SharedPreferences
     * @param context Activity 上下文环境
     * @param token Oauth2AccessToken
     */
    public static void keepOAuthV2(Context context, OAuthV2 oAuth) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString("accessToken", oAuth.getAccessToken());
        editor.putString("appFrom", oAuth.getAppFrom());
        editor.putString("authorizeCode", oAuth.getAuthorizeCode());
        editor.putString("clientId", oAuth.getClientId());
        editor.putString("clientIP", oAuth.getClientIP());
        editor.putString("clientSecret", oAuth.getClientSecret());
        editor.putString("expiresIn", oAuth.getExpiresIn());
        editor.putString("grantType", oAuth.getGrantType());
        editor.putString("msg", oAuth.getMsg());
        editor.putString("oauthVersion", oAuth.getOauthVersion());
        editor.putString("openid", oAuth.getOpenid());
        editor.putString("openkey", oAuth.getOpenkey());
        editor.putString("redirectUri", oAuth.getRedirectUri());
        editor.putString("refreshToken", oAuth.getRefreshToken());
        editor.putString("responseType", oAuth.getResponeType());
        editor.putString("scope", oAuth.getScope());
        editor.putString("seqid", oAuth.getSeqId());
        editor.putString("type", oAuth.getType());
        editor.putInt("status", oAuth.getStatus());
        editor.commit();
    }
    /**
     * 清空sharepreference
     * @param context
     */
    public static void clear(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 从SharedPreferences读取accessstoken
     * @param context
     * @return Oauth2AccessToken
     */
    public static OAuthV2 readOAuthV2(Context context){
        OAuthV2  oAuth;
        oAuth=new OAuthV2(Constants.TECENT_REDIRECT_URL);
        oAuth.setClientId(Constants.APP_KEY);
        oAuth.setClientSecret(Constants.APP_SECRET);
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        
        oAuth.setAccessToken(pref.getString("accessToken", null));
        oAuth.setAppFrom(pref.getString("appFrom", "android-sdk-1.0"));
        oAuth.setAuthorizeCode(pref.getString("authorizeCode", null));
        oAuth.setClientIP(pref.getString("clientIP", "127.0.0.1"));
        oAuth.setExpiresIn(pref.getString("expiresIn", null));
        oAuth.setGrantType(pref.getString("grantType", "authorization_code"));
        oAuth.setMsg(pref.getString("msg", null));
        oAuth.setOauthVersion(pref.getString("oauthVersion", OAuthConstants.OAUTH_VERSION_2_A));
        oAuth.setOpenid(pref.getString("openid", null));
        oAuth.setOpenkey(pref.getString("openkey", null));
        oAuth.setRefreshToken(pref.getString("refreshToken", null));
        oAuth.setResponseType(pref.getString("responseType", "code"));
        oAuth.setScope(pref.getString("scope", "all"));
        oAuth.setSeqId(pref.getString("seqid", null));
        oAuth.setStatus(pref.getInt("status", 0));
        oAuth.setType(pref.getString("type", "default"));
        return oAuth;
    }
    
    /**
     *  AccessToken是否有效,如果accessToken为空或者expiresTime过期，返回false，否则返回true
     *  @return 如果accessToken为空或者expiresTime过期，返回false，否则返回true
     */
    public static boolean isSessionValid(Context context) {
        OAuthV2  oAuth= new OAuthV2();
        oAuth = TecentTokenKeeper.readOAuthV2(context);
        long mExpiresTime;
        try {
            mExpiresTime = Long.parseLong(oAuth.getExpiresIn());
        } catch (Exception e) {
            e.getMessage();
            mExpiresTime = 0;
        }
        return (!(TextUtils.isEmpty(oAuth.getAccessToken()) || (mExpiresTime == 0)));
    }
}
