package com.roma.android.sihmi.utils;

public class Query {

    // object
    public static String selectUserWithoutSecondnSuperAdmin = "SELECT * FROM Contact WHERE (id_level != 19 AND id_level !=20)";

    // count
    public static String countUserWithoutSecondnSuperAdmin = "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20)";

    public static String ReportKaderAdmin1(String komisariat){
        return "SELECT * FROM Training WHERE (id_level != 19 AND id_level !=20) AND komisariat = '"+komisariat+"'";
    }

    public static String countReportKaderAdmin1(String komisariat){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND komisariat = '"+komisariat+"'";
    }

    public static String countReportKaderAdmin1L(String komisariat){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND komisariat = '"+komisariat+"' AND jenis_kelamin = '0'";
    }
    public static String countReportKaderAdmin1P(String komisariat){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND komisariat = '"+komisariat+"' AND jenis_kelamin = '1'";
    }

    public static String countPelatihanAdmin1(String komisariat){
        return "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20) AND komisariat = '"+komisariat+"' AND (tahun != 8 AND tahun != 200 AND tahun != 255 AND tahun != 999 AND tahun != 1875) ";
    }

    // report kader admin 2 ini sama dengan LA1 dan Admin 3(namun di admin3 menggunakan domisili_cabang)
    public static String ReportKaderAdmin2(String cabang){
        return "SELECT * FROM Training WHERE (id_level != 19 AND id_level !=20) AND cabang = '"+cabang+"'";
    }

    public static String countReportKaderAdmin2(String cabang){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND cabang = '"+cabang+"'";
    }
    public static String countReportKaderAdmin2L(String cabang){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND cabang = '"+cabang+"' AND jenis_kelamin = '0'";
    }
    public static String countReportKaderAdmin2P(String cabang){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND cabang = '"+cabang+"' AND jenis_kelamin = '1'";
    }

    public static String countPelatihanAdmin2(String cabang){
        return "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20) AND cabang = '"+cabang+"' AND (tahun != 8 AND tahun != 200 AND tahun != 255 AND tahun != 999 AND tahun != 1875) ";
    }

    // report kader la2 ini sama dengan second admin
    public static String ReportKaderLA2(){
        return "SELECT * FROM Training WHERE (id_level != 19 AND id_level !=20)";
    }

    public static String countReportKaderLA2(){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20)";
    }
    public static String countReportKaderLA2L(){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND jenis_kelamin = '0'";
    }
    public static String countReportKaderLA2P(){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND jenis_kelamin = '1'";
    }

    public static String countPelatihanLA2(){
        return "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20)  AND (tahun != 8 AND tahun != 200 AND tahun != 255 AND tahun != 999 AND tahun != 1875) ";
    }

    public static String ReportAlumniAdmin3(String domisili_cabang){
        return "SELECT * FROM Contact WHERE (id_level != 19 AND id_level !=20) AND domisili_cabang = '"+domisili_cabang+"'";
    }

    public static String countReportAlumniAdmin3(String domisili_cabang){
        return "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND domisili_cabang = '"+domisili_cabang+"'";
    }

    // Super Admin
    public static String ReportSuperAdmin(){
        return "SELECT * FROM Contact WHERE (id_level != 19 AND id_level !=20 and id_level != 1)";
    }
    public static String ReportSuperAdminNonLK(){
        return "SELECT * FROM Contact WHERE id_level = 1";
    }
    public static String ReportSuperAdminLK(){
        return "SELECT * FROM Contact WHERE id_level = 2";
    }
    public static String ReportSuperAdmin(int tahun){
        return "SELECT * FROM Contact WHERE id_roles = '" + Constant.LK_1 + "' AND tahun_daftar = '"+tahun+"'";
    }
    public static String ReportSuperAdminNonLK(int tahun){
        return "SELECT * FROM Contact WHERE id_level = 1  AND tahun_daftar = '"+tahun+"'";
    }
    public static String ReportSuperAdminLK(int tahun){
        return "SELECT * FROM Contact WHERE id_level = 2  AND tahun_daftar = '"+tahun+"'";
    }

    public static String countReportSuperAdmin(int tahun){
        return "SELECT COUNT (*) FROM Contact WHERE id_roles = '" + Constant.LK_1 + "' AND tahun_daftar = '"+tahun+"'";
    }
    public static String countReportSuperAdminNonLK(int tahun){
        return "SELECT COUNT (*) FROM Contact WHERE id_level = 1  AND tahun_daftar = '"+tahun+"'";
    }
    public static String countReportSuperAdminLK(int tahun){
        return "SELECT COUNT (*) FROM Contact WHERE id_level = 2  AND tahun_daftar = '"+tahun+"'";
    }

    public static String countReportSuperAdmin(){
        return "SELECT COUNT (*) FROM Contact WHERE id_roles = '" + Constant.LK_1 + "'";
    }
    public static String countReportSuperAdminNonLK(){
        return "SELECT COUNT (*) FROM Contact WHERE id_level = 1 ";
    }
    public static String countReportSuperAdminLK(){
        return "SELECT COUNT (*) FROM Contact WHERE id_level = 2";
    }

}
