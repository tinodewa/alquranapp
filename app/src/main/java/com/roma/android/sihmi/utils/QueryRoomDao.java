package com.roma.android.sihmi.utils;

import android.util.Log;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.Training;

import java.util.ArrayList;
import java.util.List;

public class QueryRoomDao {
//    SELECT * FROM Contact WHERE   badko IS NOT NULL   AND   badko NOT LIKE ''   AND   badko NOT LIKE ' ' ;
//    SELECT * FROM Contact WHERE badko = '' AND id_level != '19' AND id_level != '20';


    public static SimpleSQLiteQuery getUser(String type, String value, String level, String gender, String year){
        String query = "SELECT * FROM Contact";
        String kondisi;

        if (type.equals(Constant.M_NASIONAL)){
            kondisi = "";
        } else if (type.equals(Constant.M_BADKO)){
            if (value.isEmpty()){
                kondisi = " WHERE badko IS NOT NULL   AND   badko NOT LIKE ''   AND   badko NOT LIKE ' '";
            } else {
                kondisi = " WHERE badko IS NOT NULL   AND   badko NOT LIKE ''   AND   badko NOT LIKE ' ' AND badko = '"+value+"'";
            }
        } else if (type.equals(Constant.M_CABANG)){
            if (value.isEmpty()){
                kondisi = " WHERE cabang IS NOT NULL   AND   cabang NOT LIKE ''   AND   cabang NOT LIKE ' '";
            } else {
                kondisi = " WHERE cabang IS NOT NULL   AND   cabang NOT LIKE ''   AND   cabang NOT LIKE ' ' AND cabang = '"+value+"'";
            }
        } else if (type.equals(Constant.M_KORKOM)){
            if (value.isEmpty()){
                kondisi = " WHERE korkom IS NOT NULL   AND   korkom NOT LIKE ''   AND   korkom NOT LIKE ' '";
            } else {
                kondisi = " WHERE korkom IS NOT NULL   AND   korkom NOT LIKE ''   AND   korkom NOT LIKE ' ' AND korkom = '"+value+"'";
            }
        } else if (type.equals(Constant.M_KOMISARIAT)){
            if (value.isEmpty()){
                kondisi = " WHERE komisariat IS NOT NULL   AND   komisariat NOT LIKE ''   AND   komisariat NOT LIKE ' '";
            } else {
                kondisi = " WHERE komisariat IS NOT NULL   AND   komisariat NOT LIKE ''   AND   komisariat NOT LIKE ' ' AND komisariat = '"+value+"'";
            }
        } else if (type.equals(Constant.M_ALUMNI)){
            if (value.isEmpty()){
                kondisi = " WHERE domisili_cabang IS NOT NULL   AND   domisili_cabang NOT LIKE ''   AND   domisili_cabang NOT LIKE ' '";
            } else {
                kondisi = " WHERE domisili_cabang IS NOT NULL   AND   domisili_cabang NOT LIKE ''   AND   domisili_cabang NOT LIKE ' ' AND domisili_cabang = '"+value+"'";
            }
        } else {
            kondisi = "";
        }

        String kondisilevel;
        if (level.isEmpty()){
            if (kondisi.isEmpty()){
                kondisilevel = " WHERE id_level != '19' AND id_level != '20'";
            } else {
                kondisilevel = " AND id_level != '19' AND id_level != '20'";
            }
        } else {
            if (kondisi.isEmpty()){
                kondisilevel = " WHERE id_level = '"+level+"'";
            } else {
                if (level.equals("1")){
                    kondisilevel = " AND id_level = '"+level+"'";
                } else {
                    kondisilevel = " AND id_level != '1' AND id_level != '19' AND id_level != '20'";
                }
            }
        }

        String kondisigender;
        if (gender.isEmpty()){
            kondisigender = "";
        } else {
            kondisigender = " AND jenis_kelamin = '"+gender+"'";
        }

        String kondisiyear;
        if (year.isEmpty()){
            kondisiyear = "";
        } else {
            kondisiyear = " AND tahun_daftar = '"+year+"'";
        }

        Log.d("romatest", "getUser: " + query + kondisi + kondisilevel + kondisigender + kondisiyear + ";");
        SimpleSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(query+kondisi+kondisilevel+kondisiyear+";");
        return sqLiteQuery;
    };

    public static SimpleSQLiteQuery getTraining(String tipe, String cabang, String gender, String year){
        String  query = "SELECT * FROM Training WHERE id_level != '20' AND id_level != '19' AND " +
                "(tahun != 8 AND tahun != 200 AND tahun != 255 AND tahun != 999 AND tahun != 1875) ";

        if (!tipe.isEmpty()){
            query += " AND tipe = '"+tipe+"' ";
        }

        if (!cabang.isEmpty()){
            query += " AND cabang = '"+cabang+"' ";
        }

        if (!gender.isEmpty()){
            query += " AND jenis_kelamin = '+"+gender+"' ";
        }

        if (!year.isEmpty()){
            query += " AND tahun = '+"+year+"' ";
        }
        query += ";";
        SimpleSQLiteQuery q = new SimpleSQLiteQuery(query);
        return q;
    }

    public static SimpleSQLiteQuery getCountMaster(String type){
        String query;
        if (type.equals(Constant.M_BADKO)){
            query = "SELECT DISTINCT badko FROM Contact";
        } else if (type.equals(Constant.M_CABANG)){
            query = "SELECT DISTINCT cabang FROM Contact";
        } else if (type.equals(Constant.M_KORKOM)){
            query = "SELECT DISTINCT korkom FROM Contact ";
        } else if (type.equals(Constant.M_KOMISARIAT)){
            query = "SELECT DISTINCT komisariat FROM Contact";
        } else if (type.equals(Constant.M_ALUMNI)){
            query = "SELECT DISTINCT domisili_cabang FROM Contact";
        } else if (type.equals(Constant.M_TRAINING)) {
            query = "SELECT DISTINCT tipe FROM Training";
        } else {
            query = "";
        }
        SimpleSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(query);
        return sqLiteQuery;
    }

//    public static List<Contact> getLaporan(String field, String value, String gender, String year){

//        // Query string
//        String queryString = new String();
//
//        List<String> lArgs = new ArrayList<>();
//
//        if (!field.trim().isEmpty()) {
//            lArgs.add(" " + field + " IS NOT NULL ");
//            lArgs.add(" " + field + " NOT LIKE '' ");
//            lArgs.add(" " + field + " NOT LIKE ' '");
//        }
//
//        if (!value.trim().isEmpty()){
//            lArgs.add(" "+field+" = '"+value+"'");
//        }
//        if (!gender.trim().isEmpty()){
//            lArgs.add(" jenis_kelamin = '"+gender+"'");
//        }
//        if (!year.trim().isEmpty()){
//            lArgs.add(" tahun_daftar = '"+year+"'");
//        }
//
//        queryString += "SELECT * FROM Contact";
//
//        for (int i=0;i<lArgs.size();i++) {
//            if (i==0) {
//                queryString += " WHERE ";
//            }
//            queryString += " " + lArgs.get(i) + " ";
//            if ((i+1) < lArgs.size()){
//                queryString+=" AND ";
//            }
//        }
//
//        // End of query string
//        queryString += ";";
//        SimpleSQLiteQuery q = new SimpleSQLiteQuery(queryString);
//        Log.d("halloooo", "cobaQueryDinamic: "+queryString);
//
//        List<Contact> count = CoreApplication.get().getAppDb().interfaceDao().rawQueryContact(q);
//        return count;
//    }

//    public static List<Contact> getTypeUser(String field, String value, String valueLevel){
//
//        // Query string
//        String queryString = new String();
//
//        List<String> lArgs = new ArrayList<>();
//        lArgs.add(" id_level != '19'  AND id_level != '20'");
//
//        if (!field.trim().isEmpty()) {
//            lArgs.add(" " + field + " IS NOT NULL ");
//            lArgs.add(" " + field + " NOT LIKE '' ");
//            lArgs.add(" " + field + " NOT LIKE ' '");
//        }
//
//        if (!value.trim().isEmpty()){
//            lArgs.add(" "+field+" = '"+value+"'");
//        }
//        if (!valueLevel.trim().isEmpty()){
//            lArgs.add(" id_level = '"+valueLevel+"'");
//        }
//
//        queryString += "SELECT * FROM Contact";
//
//        for (int i=0;i<lArgs.size();i++) {
//            if (i==0) {
//                queryString += " WHERE ";
//            }
//            queryString += " " + lArgs.get(i) + " ";
//            if ((i+1) < lArgs.size()){
//                queryString+=" AND ";
//            }
//        }
//
//        // End of query string
//        queryString += ";";
//        Log.d("romatest", "getTypeUser: "+queryString);
//        SimpleSQLiteQuery q = new SimpleSQLiteQuery(queryString);
//
//        List<Contact> count = CoreApplication.get().getAppDb().interfaceDao().rawQueryContact(q);
//        return count;
//    }

//    public static List<Contact> getTypeUser(String field, String value, String valueLevel, String tahun){
//
//        // Query string
//        String queryString = new String();
//
//        List<String> lArgs = new ArrayList<>();
//        lArgs.add(" id_level != '19'  AND id_level != '20'");
//
//        if (!field.trim().isEmpty()) {
//            lArgs.add(" " + field + " IS NOT NULL ");
//            lArgs.add(" " + field + " NOT LIKE '' ");
//            lArgs.add(" " + field + " NOT LIKE ' '");
//        }
//
//        if (!value.trim().isEmpty()){
//            lArgs.add(" "+field+" = '"+value+"'");
//        }
//        if (!valueLevel.trim().isEmpty()){
//            lArgs.add(" id_level = '"+valueLevel+"'");
//        }
//
//        queryString += "SELECT * FROM Contact";
//
//        for (int i=0;i<lArgs.size();i++) {
//            if (i==0) {
//                queryString += " WHERE ";
//            }
//            queryString += " " + lArgs.get(i) + " ";
//            if ((i+1) < lArgs.size()){
//                queryString+=" AND ";
//            }
//        }
//
//        // End of query string
//        queryString += "AND tahun_daftar = '"+tahun+"';";
//        Log.d("haaallloooo", "getTypeUser: "+queryString);
//        SimpleSQLiteQuery q = new SimpleSQLiteQuery(queryString);
//
//        List<Contact> count = CoreApplication.get().getAppDb().interfaceDao().rawQueryContact(q);
//        return count;
//    }

//    public static List<Training> getTraininga(String tipe, String value, String gender, String year){
//
//        // Query string
//        String queryString = new String();
//
//        List<String> lArgs = new ArrayList<>();
//
//        queryString += "SELECT * FROM Training ";
//        if (!tipe.isEmpty()){
//            queryString += "WHERE tipe = '"+tipe+"' ";
//        }
//
//        if (!value.isEmpty()){
//            queryString += "AND cabang = '"+value+"' ";
//        }
//
//        if (!gender.isEmpty()){
//            queryString += "AND jenis_kelamin = '+"+gender+"' ";
//        }
//
//        if (!year.isEmpty()){
//            queryString += "AND tahun = '+"+year+"' ";
//        }
//        queryString += ";";
//        SimpleSQLiteQuery q = new SimpleSQLiteQuery(queryString);
//
//        Log.d("hallooo", "getTraining: "+queryString);
//        List<Training> count = CoreApplication.get().getAppDb().interfaceDao().rawQueryTraining(q);
//        return count;
//    }
}
