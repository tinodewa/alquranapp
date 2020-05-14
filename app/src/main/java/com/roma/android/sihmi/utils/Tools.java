package com.roma.android.sihmi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Data;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.roma.android.sihmi.ListenerHelper;
import com.roma.android.sihmi.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.service.AgendaWorkManager;
import com.roma.android.sihmi.view.adapter.ChatAdapter;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Tools {

    static ProgressDialog dialog;

    private int type=0;

    public static long getStartCurrentDayMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static String formatDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public static String getTimeFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(currentDate);
    }

    public static Long getMillisFromTimeStr(String dateStr, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date date = sdf.parse(dateStr);
        return date.getTime();
    }

    public static String getTimeAMPMFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm a");
        return df.format(currentDate);
    }

    public static String getDateFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        return df.format(currentDate);
    }

    public static String getDateLaporanFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(currentDate);
    }

    public static String getDateTimeLaporanFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        return df.format(currentDate);
    }

    public static String getYearFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy");
        return df.format(currentDate);
    }

    public static String getFullDateFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEEE, dd MMM yyyy");
        return df.format(currentDate);
    }

    public static String longToDate(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return mDay + "-" + mMonth + "-" + mYear + "\n" + hours + ":" + minutes + ":" + second;
    }

    public static String dateNow(){
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        return format.format(new Date());
    }

    public static String changeFormateDate(String date){
        final String OLD_FORMAT = "dd-MM-yyyy";
        final String NEW_FORMAT = "MM-yyyy";

        String oldDateString = "17-10-2016";
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        return newDateString;
    }

    public static String convertUTF8ToString(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    // convert internal Java String format to UTF-8
    public static String convertStringToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public static void showProgressDialog(Context context, String message) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
//        dialog.setCancelable(false);
        dialog.show();
    }

    public static void dissmissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            Log.d("check", "isInternetAvailable: goLogin " + ipAddr);
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            Log.d("check", "isInternetAvailable: goLogin e " + e.getMessage());
            return false;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        View view = toast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(context.getResources().getColor(R.color.colorTextDark), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.colorBgWhite));

        toast.show();
    }

    public static void showDialogAlert(Context context, String message){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public static void showDialogRb(Context context, String user_id, ChatAdapter adapter){
        CharSequence[] grpName = context.getResources().getStringArray(R.array.notfikasi_chat_array);
        Contact contact = CoreApplication.get().getConstant().getContactDao().getContactById(user_id);
        int pos = contact.isBisukan() ? 1 : 0;

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, pos, (dialog1, which) -> {
                    boolean bisu = which != 0;
                    contact.setBisukan(bisu);
                    CoreApplication.get().getConstant().getContactDao().insertContact(contact);
                    adapter.notifyDataSetChanged();
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

    public static void showDialogAgendaRb(Context context, Agenda agenda){
//        String agenda_id = agenda.get_id();
//        String[] grpName = context.getResources().getStringArray(R.array.pemberitahuan_agenda_array);
//        String msg = context.getResources().getString(R.string.pemberitahuan_agenda);
//        boolean isReminder = CoreApplication.get().getAppDb().interfaceDao().getAgendaById(agenda_id).isReminder();
//        int pos = isReminder ? 0 : 1;
//
//        String sDate1="28/10/2019 20:49:00";
//        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        long dateCustom;
//        try {
//            Date date1=formatter1.parse(sDate1);
//            dateCustom = date1.getTime();
//
//        TextView title = new TextView(context);
//        title.setText(msg);
//        title.setPadding(10, 10, 10, 10);
//        title.setGravity(Gravity.CENTER);
//        title.setTextSize(12);
//        AlertDialog dialog = new AlertDialog.Builder(context)
//                .setCustomTitle(title)
//                .setSingleChoiceItems(grpName, pos, (dialog1, which) -> {
//                    boolean reminder;
//                    if (which == 0){
//                        reminder = true;
////                        long reminderAlamat = agenda.getDate_expired() - System.currentTimeMillis() - (1000 * 60 * 10);
//                        long reminderAlamat = dateCustom - System.currentTimeMillis() - (1000 * 60 * 10);
//                        String[] type = agenda.getType().split("-");
//                        int id = Integer.valueOf(type[0]);
//                        String desc = type[1];
//
//                        if (reminderAlamat < 0){
////                            reminderAlamat = 60000;
//                            reminderAlamat = 5000;
//                        }
//                        Data data = createWorkInputData(agenda.getNama(), desc, id);
//                        AgendaWorkManager.scheduleReminder(reminderAlamat, data , agenda_id);
//                    } else {
//                        reminder = false;
//                        AgendaWorkManager.cancelReminder(agenda_id);
//                    }
//                    CoreApplication.get().getAppDb().interfaceDao().updateReminderAgenda(agenda_id, reminder);
//                    dialog1.dismiss();
//                })
//                .create();
//        dialog.show();

//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    private static Data createWorkInputData(String title, String text, int id){
        return new Data.Builder()
                .putString("judul", title)
                .putString("textt", text)
                .putInt("id", id)
                .build();
    }

    public static void showDialogGender(Context context, ListenerHelper listenerHelper){
        String[] grpName = context.getResources().getStringArray(R.array.gender);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(String.valueOf(which));
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static void showDialogAdmin(Context context, ListenerHelper listenerHelper){
//        String[] grpName = {"Admin 1", "Admin 2", "Admin 3", "Low Admin 1", "Low Admin 2", "Second Admin"};
        // ganti nama role admin
//        String[] grpName = {"Admin Komisariat", "Admin BPL", "Admin Alumni", "Admin Cabang", "Admin PBHMI", "Admin Nasional"};
        String[] grpName = {"Admin Komisariat", "Admin BPL", "Admin Alumni", "Admin Cabang", "Admin PBHMI"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(grpName[which]);
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static void showDialogStatus(Context context, ListenerHelper listenerHelper){
        String[] grpName = context.getResources().getStringArray(R.array.status);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(grpName[which]);
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static void showDialogLK1(Context context, String[] list, ListenerHelper listenerHelper){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(list[which]);
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public interface ListenerSelect {
        public void dialogSelect(String res, int index);
    }

    public static void showDialogLK1(Context context, String[] list, ListenerSelect listenerSelect){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerSelect.dialogSelect(list[which], which);
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    // konfirmasi delete data dialog
    public static void deleteDialog(Context context, String message, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.komfirmasi_title))
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(Constant.HAPUS);
                    }
                }).setNegativeButton(context.getString(R.string.tidak), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    // konfirmasi delete data dialog
    public static void reseteDialog(Context context, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.komfirmasi_title))
                .setMessage(R.string.konfirmasi_reset)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes("reset");
                    }
                }).setNegativeButton(context.getString(R.string.tidak), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void showDialogCustom(Context context, String title, String message, String ket, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(ket);
                        dialog.dismiss();
                    }
                }).setNegativeButton(context.getString(R.string.tidak), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void showDialogCustom(Context context, String title, String message, String positiveButton, String ket, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(ket);
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void showDialogCustom(Context context, String title, String message, String textButton){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(textButton, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // konfirmasi delete account dialog
    public static void deleteAccountDialog(Context context, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.komfirmasi_title))
                .setMessage(R.string.konfirmasi_akun)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.hapus), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(Constant.HAPUS);
                        dialog.dismiss();
                    }
                }).setNegativeButton(context.getString(R.string.batal), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    // dialog untuk super admin dan second admin
    public static void showDialogType(Context context, ListenerHelper listenerHelper){
        String[] list = {"Cabang", "Komisariat", "Nasional"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(list[which]);
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    // dialog tindakan
    public static void showDialogTindakan(Context context, ListenerHelper listenerHelper) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_tindakan, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        dialog.show();
        dialogView.findViewById(R.id.tv_lihat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.tv_ubah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listenerHelper.dialogYes(Constant.UBAH);
            }
        });
        dialogView.findViewById(R.id.tv_hapus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listenerHelper.dialogYes(Constant.HAPUS);
            }
        });
    }

    public static void showDialogTindakan(Context context, ListenerHelper listenerHelper, boolean allowUpdate, boolean allowDelete) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_tindakan, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        dialog.show();
        dialogView.findViewById(R.id.tv_lihat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.tv_ubah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listenerHelper.dialogYes(Constant.UBAH);
            }
        });
        dialogView.findViewById(R.id.tv_hapus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listenerHelper.dialogYes(Constant.HAPUS);
            }
        });
        if (allowDelete){
            dialogView.findViewById(R.id.tv_hapus).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.tv_hapus).setVisibility(View.GONE);
        }
    }

    public static void confirmDelete(Context context, ListenerHelper listenerHelper) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.komfirmasi_title));
        builder.setMessage(R.string.konfirmasi);
        builder.setPositiveButton(context.getString(R.string.ya), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listenerHelper.dialogYes(Constant.HAPUS);
            }
        });
        builder.setNegativeButton(context.getString(R.string.tidak), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialogNamaType(Context context, String[] list, ListenerHelper listenerHelper) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerHelper.dialogYes(list[which]);
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static int getType(String text){
        int angka;
        if (text.toLowerCase().contains("nasional")){
            angka = 0;
        } else if (text.toLowerCase().contains("cabang")){
            angka = 1;
        } else if (text.toLowerCase().contains("komisariat")){
            angka = 2;
        } else {
            angka = 0;
        }
        return angka;
    }

    public static String getStringType(String text){
        String nama;
        if (text.toLowerCase().contains("0")){
            nama = "Nasional";
        } else if (text.toLowerCase().contains("2")){
            nama = "Cabang";
        } else if (text.toLowerCase().contains("4")){
            nama = "Komisariat";
        } else {
            nama = "Nasional";
        }
        return nama;
    }

//    public static List<String> cabang(){
//        List<String> list = new ArrayList<>();
//        list.add("Cabang Malang");
//        list.add("Cabang Surabaya");
//        return list;
//    }
//
//    public static List<String> komisariat(){
//        List<String> list = new ArrayList<>();
//        list.add("Komisariat UB");
//        list.add("Komisariat UM");
//        return list;
//    }

//    private void showDialog(String id) {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View dialogView = inflater.inflate(R.layout.dialog_tindakan, null);
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(dialogView)
//                .create();
//        dialog.show();
//        dialogView.findViewById(R.id.tv_ubah).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                startActivityForResult(new Intent(HelpActivity.this, HelpFormActivity.class).putExtra(HelpFormActivity.IS_NEW, false).putExtra(HelpFormActivity.ID_HELP, id), Constant.REQUEST_BANTUAN);
//            }
//        });
//        dialogView.findViewById(R.id.tv_hapus).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                confirmDelete(id);
//            }
//        });
//    }
//
//
//    private void deleteData(String id) {
////        Tools.showProgressDialog(this, "Menghapus...");
//    }

    public static boolean isNonLK(){
        return Constant.getIdRoles().equals(Constant.NON_LK);
    }

    public static boolean isLK(){
        return Constant.getIdRoles().equals(Constant.LK_1);
    }

    public static boolean isAdmin1(){
        return Constant.getIdRoles().equals(Constant.ADMIN_1);
    }

    public static boolean isAdmin2(){
        return Constant.getIdRoles().equals(Constant.ADMIN_2);
    }

    public static boolean isAdmin3(){
        return Constant.getIdRoles().equals(Constant.ADMIN_3);
    }

    public static boolean isLA1(){
        return Constant.getIdRoles().equals(Constant.LOW_ADMIN_1);
    }

    public static boolean isLA2(){
        return Constant.getIdRoles().equals(Constant.LOW_ADMIN_2);
    }

    public static boolean isSecondAdmin(){
        return Constant.getIdRoles().equals(Constant.SECOND_ADMIN);
    }

    public static boolean isSuperAdmin(){
        return Constant.getIdRoles().equals(Constant.SUPER_ADMIN);
    }

    public static void visibilityFab(FloatingActionButton fab){
        if (isSuperAdmin() || isSecondAdmin()){
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    public static void initial(ImageView imageView, String initial){
        String first = String.valueOf(initial.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(first);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(first, color);
        imageView.setImageDrawable(drawable);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }


        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 96; // Replaced the 1 by a 96
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 96; // Replaced the 1 by a 96

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getInitial(char c) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(c);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(c), color);
        return drawableToBitmap(drawable);
    }

    public static void showDateDialog(Activity activity, EditText editText){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            editText.setText(dateFormatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public static void setText(EditText editText, String text){
        if (text != null && !text.trim().isEmpty()){
            editText.setText(text);
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static boolean isScreenRotated(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getRotation() != Surface.ROTATION_0;
    }

}
