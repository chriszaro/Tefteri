package com.example.database;

import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Class for the camera settings handling
 */
public class CameraScanner {
    private boolean isMultiScanMode;    // flag for multiple scan mode

    MainActivity mainActivity;  // mainActivity object to call bar-launchers

    /**
     * Constructor
     * @param activity the activity that calls the scanner (in this app is always the MainActivity)
     */
    CameraScanner(MainActivity activity){
        isMultiScanMode = false;
        mainActivity = activity;
    }

    /**
     * setter for isMultiScanMode
     * @param multiScanMode true/false boolean
     */
    public void setMultiScanMode(boolean multiScanMode) {
        isMultiScanMode = multiScanMode;
    }

    /**
     * getter for isMultiScanMode
     * @return the isMultiScanMode boolean
     */
    boolean getMultiScanMode(){
        return isMultiScanMode;
    }

    /**
     * A method to set the options of the scanner before calling the bar-launcher.
     * @param edit flag boolean for edit mode
     */
    void scanCode(boolean edit){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScannerActivity.class);
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(false);
        if (edit)
            mainActivity.barLauncherEditMode.launch(options);
        else
            mainActivity.barLauncherDefault.launch(options);
    }
}
