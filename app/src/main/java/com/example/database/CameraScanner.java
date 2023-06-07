package com.example.database;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanOptions;

public class CameraScanner extends AppCompatActivity{
//    private String link;
    private boolean isMultiScanMode;

    MainActivity mainActivity;

    CameraScanner(MainActivity activity){
//        link = "";
        isMultiScanMode = false;
        mainActivity = activity;
    }

    public void setMultiScanMode(boolean multiScanMode) {
        isMultiScanMode = multiScanMode;
    }

    Boolean getMultiScanMode(){
        return isMultiScanMode;
    }

    void scanCode(Boolean edit){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScannerActivity.class);
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(false);
        if (edit) {
            mainActivity.barLauncher2.launch(options);
        } else {
            mainActivity.barLauncher.launch(options);
        }
    }

//    public String getLink() {
//        return link;
//    }

//    // For normal and multiple scan
//    ActivityResultLauncher<ScanOptions> barLauncher =
//            mainActivity.registerForActivityResult(new ScanContract(), result -> {
//                link = result.getContents();
//                if (link != null) {
//                    String temp = mainActivity.downloadReceipt(link);
//                    if (temp != null) {
//                        mainActivity.lastReceiptID = temp;
//                    }
//
//                    if (isMultiScanMode) {
//                        AlertDialog dialog = new AlertDialog.Builder(mainActivity)
//                                .setTitle("Continue scanning?")
//                                .setMessage("Do you want to continue scanning?")
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // Continue scanning
//                                        scanCode(false);
//                                    }
//                                })
//                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // Stop scanning
//                                        isMultiScanMode = false;
//                                    }
//                                })
//                                .show();
//
//                        // After 5 seconds, treat non-responses as "Yes"
//                        final Timer t = new Timer();
//                        t.schedule(new TimerTask() {
//                            public void run() {
//                                dialog.dismiss(); // close dialog
//                                t.cancel(); // cancel also the Timer
//                                mainActivity.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        scanCode(false); // Continue scanning
//                                    }
//                                });
//                            }
//                        }, 1); // 1 millisecond
//                    }
//                }
//            });
//
//    //For scan and edit
//    ActivityResultLauncher<ScanOptions> barLauncher2 =
//            mainActivity.registerForActivityResult(
//                    new ScanContract(), result -> {
//                        link = result.getContents();
//                        if (link != null) {
//                            String temp = mainActivity.downloadReceipt(link);
//                            if (temp != null) {
//                                mainActivity.lastReceiptID = temp;
//                                CameraScanCallback callback = (CameraScanCallback) mainActivity.mainContext;
//                                onScanComplete();
//                            }
//                        }
//                    }
//            );
//
//
//    @Override
//    public void onScanComplete() {
//        // Code to be executed after the camera scan is done
//
//        //Create the Intent to start the AddProductScreen Activity
//        Intent i = new Intent(this, ReceiptScreen.class);
//
//        //Pass data to the AddProductScreen Activity through the Intent
//        i.putExtra("editBoolean", true);
//        i.putExtra("newReceipt", true);
//        i.putExtra("id", mainActivity.lastReceiptID);
//
//        //Ask Android to start the new Activity
//        startActivity(i);
//    }
}
