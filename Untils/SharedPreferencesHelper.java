package com.gengy.control.Untils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SharedPreferencesHelper {
    private static final String file_name = "sp_name";
    private static final int sp_mode = Context.MODE_PRIVATE;
    private static Context mcontext;
    public static SharedPreferencesHelper preferencesUtil;

    public static SharedPreferencesHelper getInstance(Context context) {
        mcontext = context;
        if (preferencesUtil == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (preferencesUtil == null) {
                    // 使用双重同步锁
                    preferencesUtil = new SharedPreferencesHelper();
                }
            }
        }
        return preferencesUtil;
    }

    public boolean put(String key, Object value) {
        SharedPreferences preferences = mcontext.getSharedPreferences(file_name, sp_mode);
        SharedPreferences.Editor edit = preferences.edit();


        if (value instanceof String) {
            if (!TextUtils.isEmpty((CharSequence) value)) {
                edit.putString(key, (String) value);
            } else {
                edit.putString(key, value + "");
            }

        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else {
            edit.putLong(key, (Long) value);
        }
        boolean commit = edit.commit();
        return commit;
    }

    public String getString(String key, String defualt) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
        return sharedPreferences.getString(key, defualt);
    }

    public int getInt(String key, int defualt) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
        return sharedPreferences.getInt(key, defualt);
    }

    public boolean getBoolean(String key, boolean defualt) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
        return sharedPreferences.getBoolean(key, defualt);
    }


    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    public static void clearLogout(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(ShareKey.ID);
        editor.remove(ShareKey.ACCOUNT);
        editor.remove(ShareKey.TOKEN);
        editor.remove(ShareKey.BIND_ACCOUNT);
        editor.remove(ShareKey.BIND_ACCOUNT_ID);
        editor.commit();
    }


    public static void clear(Context context,String key) {
        SharedPreferences preferences = context.getSharedPreferences(file_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * //     * 保存数据 , 所有的类型都适用
     * //     *
     * //     * @param key
     * //     * @param object
     * //
     */
    public synchronized void saveParam(String key, Object object) {
        SharedPreferences preferences = mcontext.getSharedPreferences(file_name, sp_mode);
        SharedPreferences.Editor editor = preferences.edit();

        // 得到object的类型
        String type = object.getClass().getSimpleName();
        if ("String".equals(type)) {
            // 保存String 类型
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            // 保存integer 类型
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            // 保存 boolean 类型
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            // 保存float类型
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            // 保存long类型
            editor.putLong(key, (Long) object);
        } else {
            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(object.getClass().getName() +
                        "必须实现Serializable接口!");
            }

            // 不是基本类型则是保存对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                String productBase64 = Base64.encodeToString(
                        baos.toByteArray(), Base64.DEFAULT);
                editor.putString(key, productBase64);
                Log.d(this.getClass().getSimpleName(), "save object success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(this.getClass().getSimpleName(), "save object error");
            }
        }
        editor.commit();
    }


    //销毁 public static void remove(String key) { SharedPreferences preferences = mcontext
    // .getSharedPreferences(file_name, sp_mode); SharedPreferences.Editor edit = preferences
    // .edit(); edit.remove(key); edit.commit(); }


//    private SharedPreferences preferences = null;
//    private SharedPreferences.Editor editor = null;
//    private Object object;
//    public static SharedPreferencesHelper preferencesUtil;
//
//    public static SharedPreferencesHelper getInstance() {
//        if (preferencesUtil == null) {
//            synchronized (SharedPreferencesHelper.class) {
//                if (preferencesUtil == null) {
//                    // 使用双重同步锁
//                    preferencesUtil = new SharedPreferencesHelper();
//                }
//            }
//        }
//        return preferencesUtil;
//    }
//
//    public void init(Context context){
//        preferences = PreferenceManager.getDefaultSharedPreferences(context
//                .getApplicationContext());
//    }
//
//
//
//    /**
//     * 保存数据 , 所有的类型都适用
//     *
//     * @param key
//     * @param object
//     */
//    public synchronized void saveParam(String key, Object object) {
//        if (editor == null)
//            editor = preferences.edit();
//        // 得到object的类型
//        String type = object.getClass().getSimpleName();
//        if ("String".equals(type)) {
//            // 保存String 类型
//            editor.putString(key, (String) object);
//        } else if ("Integer".equals(type)) {
//            // 保存integer 类型
//            editor.putInt(key, (Integer) object);
//        } else if ("Boolean".equals(type)) {
//            // 保存 boolean 类型
//            editor.putBoolean(key, (Boolean) object);
//        } else if ("Float".equals(type)) {
//            // 保存float类型
//            editor.putFloat(key, (Float) object);
//        } else if ("Long".equals(type)) {
//            // 保存long类型
//            editor.putLong(key, (Long) object);
//        } else {
//            if (!(object instanceof Serializable)) {
//                throw new IllegalArgumentException(object.getClass().getName() + "
// 必须实现Serializable接口!");
//            }
//
//            // 不是基本类型则是保存对象
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            try {
//                ObjectOutputStream oos = new ObjectOutputStream(baos);
//                oos.writeObject(object);
//                String productBase64 = Base64.encodeToString(
//                        baos.toByteArray(), Base64.DEFAULT);
//                editor.putString(key, productBase64);
//                Log.d(this.getClass().getSimpleName(), "save object success");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(this.getClass().getSimpleName(), "save object error");
//            }
//        }
//        editor.commit();
//    }
//
//    /**
//     * 移除信息
//     */
//    public synchronized void remove(String key) {
//        if (editor == null)
//            editor = preferences.edit();
//        editor.remove(key);
//        editor.commit();
//    }
//
//
//    /**
//     * 得到保存数据的方法，所有类型都适用
//     *
//     * @param key
//     * @param defaultObject
//     * @return
//     */
//    public Object getParam(String key, Object defaultObject) {
//        if (defaultObject == null) {
//            return getObject(key);
//        }
//
//        String type = defaultObject.getClass().getSimpleName();
//
//        if ("String".equals(type)) {
//            return preferences.getString(key, (String) defaultObject);
//        } else if ("Integer".equals(type)) {
//            return preferences.getInt(key, (Integer) defaultObject);
//        } else if ("Boolean".equals(type)) {
//            return preferences.getBoolean(key, (Boolean) defaultObject);
//        } else if ("Float".equals(type)) {
//            return preferences.getFloat(key, (Float) defaultObject);
//        } else if ("Long".equals(type)) {
//            return preferences.getLong(key, (Long) defaultObject);
//        }
//        return getObject(key);
//    }
//
//    /**
//     * Whether to use for the first time
//     *
//     * @return
//     */
//    public boolean isFirst() {
//        return (Boolean) getParam("isFirst", true);
//    }
//
//    /**
//     * set user first use is false
//     *
//     * @return
//     */
//    public void setFirst(Boolean isFirst) {
//        saveParam("isFirst", isFirst);
//    }
//
//    /**
//     * Set up the first time login
//     *
//     * @return
//     */
//    public boolean isLogin() {
//        return (Boolean) getParam("isLogin", false);
//    }
//
//    /**
//     * @return
//     */
//    public void setLogin(Boolean isLogin) {
//        saveParam("isLogin", isLogin);
//    }
//
//    public Object getObject(String key) {
//        String wordBase64 = preferences.getString(key, "");
//        byte[] base64 = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
//        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
//        try {
//            ObjectInputStream bis = new ObjectInputStream(bais);
//            object =  bis.readObject();
//            Log.d(this.getClass().getSimpleName(), "Get object success");
//            return object;
//        } catch (Exception e) {
//
//        }
//        Log.e(this.getClass().getSimpleName(), "Get object is error");
//        return null;
//    }

}
