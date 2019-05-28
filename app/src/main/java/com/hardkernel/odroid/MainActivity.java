package com.hardkernel.odroid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.ToggleButton;

import android.net.IpConfiguration;
import android.net.IpConfiguration.ProxySettings;
import android.net.IpConfiguration.IpAssignment;
import android.net.LinkAddress;
import android.net.StaticIpConfiguration;
import android.net.NetworkUtils;
import android.net.EthernetManager;
import java.net.InetAddress;
import java.net.Inet4Address;


import static java.lang.System.*;

public class MainActivity extends Activity {

    private final static String TAG = "ODROIDUtility";
    public final static String GOVERNOR_NODE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    public final static String SCALING_AVAILABLE_GOVERNORS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    public final static String DRAM_SCALING_AVAILABLE_GOVERNORS = "/sys/devices/platform/exynos5-devfreq-mif/devfreq/exynos5-devfreq-mif/available_governors";
    public final static String DRAM_SCALING_AVAILABLE_FREQUENCY = "/sys/devices/platform/exynos5-devfreq-mif/devfreq/exynos5-devfreq-mif/available_frequencies";
    public final static String DRAM_GOVERNOR_NODE = "/sys/devices/platform/exynos5-devfreq-mif/devfreq/exynos5-devfreq-mif/governor";
    public final static String DRAM_FREQUENCY_NODE = "/sys/devices/platform/exynos5-devfreq-mif/devfreq/exynos5-devfreq-mif/max_freq";
    private final static String BOOT_INI = "/storage/0000-3333/boot.ini";
    private final static String BT_PROP = "persist.disable_bluetooth";
    private final static String BT_SINK_PROP = "persist.service.bt.a2dp.sink";
    private final static String SHUT_PROP = "persist.pwbtn.shutdown";
    private final static String FORCE_HDMI_AUDIO_PROP = "persist.hdmi.audioforce";
    private final static String FORCE_HDMI_INPUT_PROP = "persist.hdmi.switch_tv_input";
    private final static String ADB_OVER_NET_PROP = "persist.adb.tcp.port";
    private final static String WLAN_NO_PS_PROP = "persist.no_wlan_ps";
    private final static String USB_NO_PERMS_PROP = "persist.disable_usb_perms";
    private final static String HDMI_ORIENTATION_PROP = "persist.demo.hdmirotation";
    private final static String SF_ROTATION_PROP = "ro.sf.hwrotation";


    private static final int DHCP = 0;
    private static final int STATIC_IP = 1;

    private Spinner mSpinnerGovernor;
    private String mGovernorString;

    private Spinner mSpinnerDRAMGovernor;
    private String mDRAMGovernorString;

    private Spinner mSpinnerDRAMFreqeuncy;
    private String mDRAMFreqeuncyString;

    private RadioButton mRadio_left;
    private RadioButton mRadio_right;

    private Spinner mSpinner_Resolution;
    private String mResolution = "720p60hz";


    private RadioButton mRadio_0;
    private RadioButton mRadio_90;
    private RadioButton mRadio_180;
    private RadioButton mRadio_270;

    private RadioGroup mRG_resolution;
    private RadioGroup mRG_phy;

    private Spinner shortcut_f7;
    private Spinner shortcut_f8;
    private Spinner shortcut_f9;
    private Spinner shortcut_f10;

    private int mDegree;

    private static Context context;

    private ToggleButton mBtnFanMode;
    private EditText mEditTextFanSpeed1;
    private EditText mEditTextFanSpeed2;
    private EditText mEditTextFanSpeed3;
    private EditText mEditTextFanSpeed4;
    private EditText mEditTextSpeeds[] = new EditText[4];
    private Button mBtnFanSpeedsApply;
    private Button mBtnGetPWMDuty;
    private EditText mEditTextPWMDuty;
    private Button mBtnPWMDutyApply;
    private ToggleButton mBtnPWMEnable;
    private EditText mEditTextTempLevels[] = new EditText[3];
    private EditText mEditTextTempLevel1;
    private EditText mEditTextTempLevel2;
    private EditText mEditTextTempLevel3;
    private Button mBtnTempLevelsApply;

    private Spinner mSpinnerEthernet;
    private LinearLayout mstaticip;
    private EditText mEditTextEthIpaddress;
    private EditText mEditTextEthGateway;
    private EditText mEditTextEthPrefix;
    private EditText mEditTextEthDns1;
    private EditText mEditTextEthDns2;

    private IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;
    private ProxySettings mProxySettings = ProxySettings.UNASSIGNED;
    private StaticIpConfiguration mStaticIpConfiguration = null;
	private EthernetManager mEthernetManager;

    private Switch mBtSwitch;
    private Switch mBtSinkSwitch;
    private Switch mShutSwitch;
    private Switch mHdmiAudioSwitch;
    private Switch mHdmiInputSwitch;
    private Switch mADBonSwitch;
    private Switch mWlanNoPsSwitch;
    private Switch mNoUSBPermsSwitch;
    private Boolean mDisablevu7 = false;
    private Boolean mTouchInvertX = false;
    private Boolean mTouchInvertY = false;
    private Boolean mDisableDP = false;
    private int mEDID = 0;
    private int mHPD = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        mRadio_left = (RadioButton)findViewById(R.id.radio_left);
        mRadio_right = (RadioButton)findViewById(R.id.radio_right);

        String line;

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabSpec tab1 = tabHost.newTabSpec("CPU");
        TabSpec tab2 = tabHost.newTabSpec("Mouse");
        TabSpec tab3 = tabHost.newTabSpec("Screen");
        TabSpec tab4 = tabHost.newTabSpec("Rotation");
        TabSpec tab5 = tabHost.newTabSpec("Fan");
        TabSpec tab6 = tabHost.newTabSpec("Ethernet");
        TabSpec tab7 = tabHost.newTabSpec("Shortcut");
        TabSpec tab8 = tabHost.newTabSpec("Misc");

        tab1.setIndicator("CPU and DRAM");
        tab1.setContent(R.id.tab1);
        tab2.setIndicator("Mouse");
        tab2.setContent(R.id.tab2);
        tab3.setIndicator("Screen");
        tab3.setContent(R.id.tab3);
        tab4.setIndicator("Rotation");
        tab4.setContent(R.id.tab4);
        tab5.setIndicator("Fan");
        tab5.setContent(R.id.tab5);
        tab6.setIndicator("Ethernet");
        tab6.setContent(R.id.tab6);
        tab7.setIndicator("Shortcut");
        tab7.setContent(R.id.tab7);
        tab8.setIndicator("Misc");
        tab8.setContent(R.id.tab8);

        tabHost.addTab(tab1);
        //tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);
        tabHost.addTab(tab5);
        tabHost.addTab(tab6);
//        tabHost.addTab(tab7);
        tabHost.addTab(tab8);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                if(tabId.equals("Fan")){
                    getFanValues();
                }
            }
        });

        mSpinnerGovernor = (Spinner) findViewById(R.id.spinner_governors);
        String[] array = getFromNode(SCALING_AVAILABLE_GOVERNORS).split(" ");
        ArrayAdapter<String> governorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, array);
        mSpinnerGovernor.setAdapter(governorAdapter);

        mSpinnerGovernor.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                String value = arg0.getItemAtPosition(arg2).toString();
                Log.e(TAG, "governor = " + value);
                setValueToNode(value, GOVERNOR_NODE);

                SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("governor", value);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        mGovernorString = getFromNode(GOVERNOR_NODE);

        Log.e(TAG, "mGovernorString = " + mGovernorString);

        if (mGovernorString != null) {
            mSpinnerGovernor.setSelection(governorAdapter.getPosition(mGovernorString));
        }

        mSpinnerDRAMGovernor = (Spinner) findViewById(R.id.spinner_dram_governors);
        array = getFromNode(DRAM_SCALING_AVAILABLE_GOVERNORS).split(" ");
        ArrayAdapter<String> DRAMgovernorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, array);
        mSpinnerDRAMGovernor.setAdapter(DRAMgovernorAdapter);

        mSpinnerDRAMGovernor.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                String value = arg0.getItemAtPosition(arg2).toString();
                Log.e(TAG, "DRAM governor = " + value);
                setValueToNode(value, DRAM_GOVERNOR_NODE);

                SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("DRAM governor", value);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        mDRAMGovernorString = getFromNode(DRAM_GOVERNOR_NODE);

        Log.e(TAG, "mDRAMGovernorString = " + mDRAMGovernorString);

        if (mDRAMGovernorString != null) {
            mSpinnerDRAMGovernor.setSelection(DRAMgovernorAdapter.getPosition(mDRAMGovernorString));
        }

        mSpinnerDRAMFreqeuncy = (Spinner) findViewById(R.id.spinner_dram_freq);
        String[] buf = getFromNode(DRAM_SCALING_AVAILABLE_FREQUENCY).split(" ");
        array = Arrays.copyOfRange(buf, buf.length - 4, buf.length);
        ArrayAdapter<String> DRAMFrequencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, array);
        mSpinnerDRAMFreqeuncy.setAdapter(DRAMFrequencyAdapter);

        mSpinnerDRAMFreqeuncy.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                String value = arg0.getItemAtPosition(arg2).toString();
                Log.e(TAG, "DRAM freq = " + value);
                setValueToNode(value, DRAM_FREQUENCY_NODE);

                SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("DRAM freq", value);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        mDRAMFreqeuncyString = getFromNode(DRAM_FREQUENCY_NODE);

        Log.e(TAG, "mDRAMFreqeuncyString = " + mDRAMFreqeuncyString);

        if (mDRAMFreqeuncyString != null) {
            mSpinnerDRAMFreqeuncy.setSelection(DRAMFrequencyAdapter.getPosition(mDRAMFreqeuncyString));
        }

        File boot_ini = new File(BOOT_INI);
        if (boot_ini.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(BOOT_INI));
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("bootargs"))
                        break;

                    if (line.startsWith("setenv disable_vu7")){
                        if (line.contains("true")) {
                            mDisablevu7 = true;
                        }
                    }

                    if (line.startsWith("setenv touch_invert_x")){
                        if (line.contains("true")) {
                            mTouchInvertX = true;
                        }
                    }

                    if (line.startsWith("setenv touch_invert_y")){
                        if (line.contains("true")) {
                            mTouchInvertY = true;
                        }
                    }
                    if (line.startsWith("setenv disable_dp")){
                        if (line.contains("true")) {
                            mDisableDP = true;
                        }
                    }
                    if (line.startsWith("setenv edid")){
                        if (line.contains("1")) {
                            mEDID = 1;
                        }
                    }

                    if (line.startsWith("setenv hpd")){
                        if (line.contains("0")) {
                            mHPD = 0;
                        }
                    }

                    if (line.contains("hdmi_phy_res") && line.indexOf("#") < 0) {
                        Log.e(TAG, line);
                        mResolution = line.substring(line.indexOf("\"") + 1, line.length() - 1);
                        Log.e(TAG, mResolution);
                    }
                }
                bufferedReader.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        mSpinner_Resolution = (Spinner)findViewById(R.id.spinner_resolution);
        ArrayAdapter<CharSequence> mAdapterResolution = ArrayAdapter.createFromResource(this,
                R.array.resolution_array, android.R.layout.simple_spinner_item);
        mAdapterResolution.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_Resolution.setAdapter(mAdapterResolution);

        mSpinner_Resolution.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                String resolution = arg0.getItemAtPosition(arg2).toString();
                if (mResolution.equals(resolution))
                    return;
                else
                    mResolution = resolution;

                Log.e(TAG, "Selected resolution = " + resolution);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        for(int i = 0; i < mAdapterResolution.getCount(); i++) {
            String item = (String)mAdapterResolution.getItem(i);
            Log.e(TAG, item);
        }

        mSpinner_Resolution.setSelection(mAdapterResolution.getPosition(mResolution));

        mRadio_left = (RadioButton)findViewById(R.id.radio_left);
        mRadio_right = (RadioButton)findViewById(R.id.radio_right);

        SharedPreferences pref = getSharedPreferences("utility", Context.MODE_PRIVATE);
        if (pref.getString("mouse", "right").equals("right"))
            mRadio_right.setChecked(true);
        else
            mRadio_left.setChecked(true);

        Button btn = (Button)findViewById(R.id.button_mouse_apply);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                if (mRadio_left.isChecked()) {
                    editor.putString("mouse", "left");
                    setMouse("left");
                } else if (mRadio_right.isChecked()) {
                    editor.putString("mouse", "right");
                    setMouse("right");
                }
                editor.apply();
            }

        });

        btn = (Button)findViewById(R.id.button_apply_reboot);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                saveBootIni();
                reboot();
            }

        });

        mDegree = SystemProperties.getInt(SF_ROTATION_PROP, 0);

        mRadio_0 = (RadioButton)findViewById(R.id.radio_0);
        mRadio_90 = (RadioButton)findViewById(R.id.radio_90);
        mRadio_180 = (RadioButton)findViewById(R.id.radio_180);
        mRadio_270 = (RadioButton)findViewById(R.id.radio_270);

        switch (mDegree) {
            case 90:
                mRadio_0.setChecked(false);
                mRadio_90.setChecked(true);
                mRadio_180.setChecked(false);
                mRadio_270.setChecked(false);
                break;
            case 180:
                mRadio_0.setChecked(false);
                mRadio_90.setChecked(false);
                mRadio_180.setChecked(true);
                mRadio_270.setChecked(false);
                break;
            case 270:
                mRadio_0.setChecked(false);
                mRadio_90.setChecked(false);
                mRadio_180.setChecked(false);
                mRadio_270.setChecked(true);
                break;
            default:
                mRadio_0.setChecked(true);
                mRadio_90.setChecked(false);
                mRadio_180.setChecked(false);
                mRadio_270.setChecked(false);
                break;
        }

        mRadio_0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDegree = 0;
            }

        });

        mRadio_90.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDegree = 90;
            }

        });

        mRadio_180.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDegree = 180;
            }

        });

        mRadio_270.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDegree = 270;
            }

        });


        btn = (Button)findViewById(R.id.button_rotation_apply);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                android.provider.Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, 0);
                android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                saveBootIni();
                reboot();
            }
        });
//        shortcutActivity();

        mBtnFanMode = (ToggleButton) findViewById(R.id.btn_fan_mode);

        mBtnFanMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int auto_manual = 0;
                if (isChecked) {
                    auto_manual = 1;
                }
                setFanMode(auto_manual);
                SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("fan_mode", auto_manual);
                editor.apply();
                getFanValues();
            }
        });

        mEditTextFanSpeed1 = (EditText) findViewById(R.id.et_speed1);
        mEditTextFanSpeed2 = (EditText) findViewById(R.id.et_speed2);
        mEditTextFanSpeed3 = (EditText) findViewById(R.id.et_speed3);
        mEditTextFanSpeed4 = (EditText) findViewById(R.id.et_speed4);

        mEditTextSpeeds[0]= mEditTextFanSpeed1;
        mEditTextSpeeds[1]= mEditTextFanSpeed2;
        mEditTextSpeeds[2]= mEditTextFanSpeed3;
        mEditTextSpeeds[3]= mEditTextFanSpeed4;

        mBtnFanSpeedsApply = (Button) findViewById(R.id.btn_speed);
        mBtnFanSpeedsApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int fan_speed[] = new int[4];
                int i = 0;
                for(EditText ed : mEditTextSpeeds) {
                    fan_speed[i++] = Integer.parseInt(ed.getText().toString());
                }
                String value;
                if ((fan_speed[0] > 0) && (fan_speed[0] < fan_speed[1]) &&
                        (fan_speed[1] < fan_speed[2]) && (fan_speed[2] < fan_speed[3]) && fan_speed[3] <= 100) {
                    value = mEditTextFanSpeed1.getText() + " " + mEditTextFanSpeed2.getText() + " " +
                            mEditTextFanSpeed3.getText() + " " + mEditTextFanSpeed4.getText();
                    Log.e(TAG, value);
                    setFanSpeeds(value);
                    SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("fan_speeds", value);
                    editor.apply();
                    getFanValues();
                } else {
                    Toast.makeText(
                            getBaseContext(), "Wrong format",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnGetPWMDuty = (Button) findViewById(R.id.btn_get_pwm_duty);
        mBtnGetPWMDuty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = readPWMDuty();
                Log.e(TAG, value);
                value = value.trim();
                mEditTextPWMDuty.setText(value);
            }
        });

        mEditTextPWMDuty = (EditText) findViewById(R.id.et_pwm_duty);
        mBtnPWMDutyApply = (Button) findViewById(R.id.btn_pwm_duty);
        mBtnPWMDutyApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int duty = Integer.parseInt(mEditTextPWMDuty.getText().toString());
                if (duty >= 0 && duty < 256) {
                    String value = mEditTextPWMDuty.getText().toString().trim();
                    setPWMDuty(value);
                    SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("pwm_duty", value);
                    editor.apply();
                    getFanValues();
                } else {
                    Toast.makeText(
                            getBaseContext(), "Wrong format",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnPWMEnable = (ToggleButton) findViewById(R.id.btn_pwm_enable);
        mBtnPWMEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int enable = 0;
                if (isChecked) {
                    enable = 1;
                }
                setPWMEnable(enable);
                SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("pwm_enable", enable);
                editor.apply();
                getFanValues();
            }
        });

        mEditTextTempLevel1 = (EditText) findViewById(R.id.et_temp1);
        mEditTextTempLevel2 = (EditText) findViewById(R.id.et_temp2);
        mEditTextTempLevel3 = (EditText) findViewById(R.id.et_temp3);

        mEditTextTempLevels[0] = mEditTextTempLevel1;
        mEditTextTempLevels[1] = mEditTextTempLevel2;
        mEditTextTempLevels[2] = mEditTextTempLevel3;

        mBtnTempLevelsApply = (Button) findViewById(R.id.btn_temp_levels);
        mBtnTempLevelsApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp_levels[] = new int[3];
                int i = 0;
                for(EditText ed : mEditTextTempLevels) {
                    temp_levels[i++] = Integer.parseInt(ed.getText().toString());
                }
                String value;
                if ((temp_levels[0] > 0) && (temp_levels[0] < temp_levels[1]) &&
                        (temp_levels[1] < temp_levels[2]) && temp_levels[2] <= 100) {
                    value = mEditTextTempLevels[0].getText() + " " + mEditTextTempLevels[1].getText() + " " +
                            mEditTextTempLevels[2].getText();
                    Log.e(TAG, value);
                    setTempLevels(value);
                    SharedPreferences pref = getSharedPreferences("utility", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("temp_levels", value);
                    editor.apply();
                    getFanValues();
                } else {
                    Toast.makeText(
                            getBaseContext(), "Wrong format",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!getFanValues())
            tabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);

        mSpinnerEthernet = (Spinner) findViewById(R.id.ip_settings);
        mstaticip = (LinearLayout) findViewById(R.id.staticip);
		mEditTextEthIpaddress = (EditText) findViewById(R.id.ipaddress);
		mEditTextEthGateway = (EditText) findViewById(R.id.gateway);
		mEditTextEthPrefix = (EditText) findViewById(R.id.network_prefix_length);
		mEditTextEthDns1 = (EditText) findViewById(R.id.dns1);
		mEditTextEthDns2 = (EditText) findViewById(R.id.dns2);
				
        mEthernetManager = (EthernetManager) getSystemService(ETHERNET_SERVICE);
        if (mEthernetManager != null){
            Log.e(TAG, "Connected to EthernetManager");
        } else {
            Log.e(TAG, "Shaytan !!!");
        }
		
		IpConfiguration config = mEthernetManager.getConfiguration();
		    if (config.getIpAssignment() == IpAssignment.STATIC) {
                mSpinnerEthernet.setSelection(STATIC_IP);
				StaticIpConfiguration staticConfig = config.getStaticIpConfiguration();
				
				if (staticConfig.ipAddress != null) {
				    mEditTextEthIpaddress.setText(
                        staticConfig.ipAddress.getAddress().getHostAddress());
				    mEditTextEthPrefix.setText(Integer.toString(staticConfig.ipAddress
                        .getNetworkPrefixLength()));
				}

                if (staticConfig.gateway != null) {
                    mEditTextEthGateway.setText(staticConfig.gateway.getHostAddress());
                }

                Iterator<InetAddress> dnsIterator = staticConfig.dnsServers.iterator();
                if (dnsIterator.hasNext()) {
                    mEditTextEthDns1.setText(dnsIterator.next().getHostAddress());
                }
                if (dnsIterator.hasNext()) {
                    mEditTextEthDns2.setText(dnsIterator.next().getHostAddress());
                }

					
			} else {
				mSpinnerEthernet.setSelection(DHCP);
			}

		
        mSpinnerEthernet.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (mSpinnerEthernet.getSelectedItemPosition() == STATIC_IP){
                    mstaticip.setVisibility(LinearLayout.VISIBLE);
                } else {
                    mstaticip.setVisibility(LinearLayout.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                    return;
            }

        });
		
		Button EthApply = (Button)findViewById(R.id.button_ethernet_apply);
        EthApply.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

		    if (ipAndProxyFieldsAreValid()){
                        mEthernetManager.setConfiguration(
                new IpConfiguration(mIpAssignment, mProxySettings,
                                    mStaticIpConfiguration, null));
			    }
            }

        });
        mBtSwitch = (Switch) findViewById(R.id.switch_bt);
        mBtSwitch.setChecked(!SystemProperties.getBoolean(BT_PROP, true));
        mBtSinkSwitch = (Switch) findViewById(R.id.switch_bt_sink);
        mBtSinkSwitch.setChecked(SystemProperties.getBoolean(BT_SINK_PROP, true));
        mShutSwitch = (Switch) findViewById(R.id.switch_shut);
        mShutSwitch.setChecked(SystemProperties.getBoolean(SHUT_PROP, false));
        mHdmiAudioSwitch = (Switch) findViewById(R.id.switch_hdmi_aud);
        mHdmiAudioSwitch.setChecked(SystemProperties.getBoolean(FORCE_HDMI_AUDIO_PROP, false));
        mHdmiInputSwitch = (Switch) findViewById(R.id.switch_hdmi_input);
        mHdmiInputSwitch.setChecked(SystemProperties.getBoolean(FORCE_HDMI_INPUT_PROP, true));
        mADBonSwitch = (Switch) findViewById(R.id.switch_adb_on);
        mADBonSwitch.setChecked((SystemProperties.getInt(ADB_OVER_NET_PROP, 0) > 0));
        mWlanNoPsSwitch = (Switch) findViewById(R.id.switch_wlan_no_ps);
        mWlanNoPsSwitch.setChecked(SystemProperties.getBoolean(WLAN_NO_PS_PROP, false));
        mNoUSBPermsSwitch = (Switch) findViewById(R.id.switch_no_usb_perms);
        mNoUSBPermsSwitch.setChecked(SystemProperties.getBoolean(USB_NO_PERMS_PROP, false));

        mBtSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(BT_PROP, "false");
                }else{
                    SystemProperties.set(BT_PROP, "true");
                }
            }
        });
        mBtSinkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(BT_SINK_PROP, "true");
                }else{
                    SystemProperties.set(BT_SINK_PROP, "false");
                }
            }
        });
        mShutSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(SHUT_PROP, "true");
                }else{
                    SystemProperties.set(SHUT_PROP, "false");
                }
            }
        });
        mHdmiAudioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(FORCE_HDMI_AUDIO_PROP, "true");
                }else{
                    SystemProperties.set(FORCE_HDMI_AUDIO_PROP, "false");
                }
            }
        });
        mHdmiInputSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(FORCE_HDMI_INPUT_PROP, "true");
                }else{
                    SystemProperties.set(FORCE_HDMI_INPUT_PROP, "false");
                }
            }
        });
        mADBonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(ADB_OVER_NET_PROP, "5555");
                }else{
                    SystemProperties.set(ADB_OVER_NET_PROP, "0");
                }
            }
        });
        mWlanNoPsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(WLAN_NO_PS_PROP, "true");
                }else{
                    SystemProperties.set(WLAN_NO_PS_PROP, "false");
                }
            }
        });
        mNoUSBPermsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SystemProperties.set(USB_NO_PERMS_PROP, "true");
                }else{
                    SystemProperties.set(USB_NO_PERMS_PROP, "false");
                }
            }
        });
    }


	private boolean ipAndProxyFieldsAreValid() {
        mIpAssignment = (mSpinnerEthernet != null &&
                mSpinnerEthernet.getSelectedItemPosition() == STATIC_IP) ?
                IpAssignment.STATIC : IpAssignment.DHCP;

        if (mIpAssignment == IpAssignment.STATIC) {
            mStaticIpConfiguration = new StaticIpConfiguration();
            int result = validateIpConfigFields(mStaticIpConfiguration);
            if (result != 0) {
                return false;
            }
        }

        return true;
    }

    private Inet4Address getIPv4Address(String text) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(text);
        } catch (IllegalArgumentException|ClassCastException e) {
            return null;
        }
    }

    private int validateIpConfigFields(StaticIpConfiguration staticIpConfiguration) {
        if (mEditTextEthIpaddress == null) return 0;

        String ipAddr = mEditTextEthIpaddress.getText().toString();
        if (TextUtils.isEmpty(ipAddr)) return -1;

        Inet4Address inetAddr = getIPv4Address(ipAddr);
        if (inetAddr == null) {
            return -1;
        }

        int networkPrefixLength = -1;
        try {
            networkPrefixLength = Integer.parseInt(mEditTextEthPrefix.getText().toString());
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                return -1;
            }
            staticIpConfiguration.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
        } catch (NumberFormatException e) {
            // Set the hint as default after user types in ip address
            mEditTextEthPrefix.setText("24");
        }

        String gateway = mEditTextEthGateway.getText().toString();
        if (TextUtils.isEmpty(gateway)) {
            try {
                //Extract a default gateway from IP address
                InetAddress netPart = NetworkUtils.getNetworkPart(inetAddr, networkPrefixLength);
                byte[] addr = netPart.getAddress();
                addr[addr.length-1] = 1;
                mEditTextEthGateway.setText(InetAddress.getByAddress(addr).getHostAddress());
            } catch (RuntimeException ee) {
            } catch (java.net.UnknownHostException u) {
            }
        } else {
            InetAddress gatewayAddr = getIPv4Address(gateway);
            if (gatewayAddr == null) {
                return -1;
            }
            staticIpConfiguration.gateway = gatewayAddr;
        }

        String dns = mEditTextEthDns1.getText().toString();
        InetAddress dnsAddr = null;

        if (TextUtils.isEmpty(dns)) {
            //If everything else is valid, provide hint as a default option
            mEditTextEthDns1.setText("8.8.8.8");
        } else {
            dnsAddr = getIPv4Address(dns);
            if (dnsAddr == null) {
                return -1;
            }
            staticIpConfiguration.dnsServers.add(dnsAddr);
        }

        if (mEditTextEthDns2.length() > 0) {
            dns = mEditTextEthDns2.getText().toString();
            dnsAddr = getIPv4Address(dns);
            if (dnsAddr == null) {
                return -1;
            }
            staticIpConfiguration.dnsServers.add(dnsAddr);
        }
        return 0;
    }


    private boolean getFanValues() {
        String value = readFanMode();

        if (value == null)
            return false;

        Log.e(TAG, value);

        if (value.contains("auto")) {
            mBtnFanMode.setChecked(true);
        } else {
            mBtnFanMode.setChecked(false);
        }

        value = readFanSpeeds();
        Log.e(TAG, value);
        int i = 0;
        for (String token : value.split(" ")) {
            token = token.trim();
            mEditTextSpeeds[i++].setText(token);
        }

        value = readPWMDuty();
        Log.e(TAG, value);
        value = value.trim();
        mEditTextPWMDuty.setText(value);

        value = readPWMEnable();
        Log.e(TAG, value);

        if (value.contains("on")) {
            mBtnPWMEnable.setChecked(true);
        } else {
            mBtnPWMEnable.setChecked(false);
        }

        value = readTempLevels();
        Log.e(TAG, value);
        i = 0;
        for (String token : value.split(" ")) {
            token = token.trim();
            mEditTextTempLevels[i++].setText(token);
        }

        return true;
    }

    public void saveBootIni() {
        File boot_ini = new File(BOOT_INI);
        if (boot_ini.exists()) {
            boot_ini.delete();
        }

        PrintWriter writer;
        try {
            writer = new PrintWriter(BOOT_INI, "UTF-8");

            writer.println("ODROIDXU-UBOOT-CONFIG\n");

            String x_res = "1280";
            String y_res = "720";
            if ("480x320p60hz".equals(mResolution)) {
                x_res = "480";
                y_res = "320";
            } else if ("480p60hz".equals(mResolution)) {
                x_res = "640";
                y_res = "480";
            } else if ("480p59.94hz".equals(mResolution)) {
                x_res = "720";
                y_res = "480";
            } else if ("576p50hz".equals(mResolution)) {
                x_res = "720";
                y_res = "576";
            } else if ("480x800p60hz".equals(mResolution)) {
                x_res = "480";
                y_res = "800";
            } else if ("800x480p60hz".equals(mResolution)
                    || "ODROID-VU5/7".equals(mResolution)) {
                mResolution = "800x480p60hz";
                x_res = "800";
                y_res = "480";
            } else if ("848x480p60hz".equals(mResolution)) {
                x_res = "848";
                y_res = "480";
            } else if ("600p60hz".equals(mResolution)) {
                x_res = "800";
                y_res = "600";
            } else if ("1024x600p60hz".equals(mResolution)
                    || "ODROID-VU7 Plus".equals(mResolution)) {
                mResolution = "1024x600p60hz";
                x_res = "1024";
                y_res = "600";
            } else if ("768p60hz".equals(mResolution)
                    || "ODROID-VU8".equals(mResolution)) {
                mResolution = "768p60hz";
                x_res = "1024";
                y_res = "768";
            } else if (mResolution.contains("720p")) {
                x_res = "1280";
                y_res = "720";
            } else if ("1280x768p60hz".equals(mResolution)) {
                x_res = "1280";
                y_res = "768";
            } else if ("1152x864p75hz".equals(mResolution)) {
                x_res = "1152";
                y_res = "864";
            } else if ("800p59hz".equals(mResolution)) {
                x_res = "1280";
                y_res = "800";
            } else if ("960p60hz".equals(mResolution)) {
                x_res = "1280";
                y_res = "960";
            } else if ("900p60hz".equals(mResolution)) {
                x_res = "1440";
                y_res = "900";
            } else if ("1024p60hz".equals(mResolution)) {
                x_res = "1280";
                y_res = "1024";
            } else if ("1400x1050p60hz".equals(mResolution)) {
                x_res = "1400";
                y_res = "1050";
            } else if ("1360x768p60hz".equals(mResolution)) {
                x_res = "1360";
                y_res = "768";
            } else if ("1600x900p60hz".equals(mResolution)) {
                x_res = "1600";
                y_res = "900";
            } else if ("1600x1200p60hz".equals(mResolution)) {
                x_res = "1600";
                y_res = "1200";
            } else if ("1920x800p60hz".equals(mResolution)) {
                x_res = "1920";
                y_res = "800";
            } else if ("1792x1344p60hz".equals(mResolution)) {
                x_res = "1792";
                y_res = "1244";
            } else if (mResolution.contains("1080")) {
                x_res = "1920";
                y_res = "1080";
            } else if ("1920x1200p60hz".equals(mResolution)) {
                x_res = "1920";
                y_res = "1200";
            } else if ("1200x1920p60hz".equals(mResolution)) {
                x_res = "1200";
                y_res = "1920";
            } else if ("720x1280p61hz".equals(mResolution)) {
                x_res = "720";
                y_res = "1280";
            }

            writer.println("# setenv fb_x_res \"640\"");
            writer.println("# setenv fb_y_res \"480\"");
            writer.println("# setenv hdmi_phy_res \"480p60hz\"\n");

            writer.println("# setenv fb_x_res \"720\"");
            writer.println("# setenv fb_y_res \"480\"");
            writer.println("# setenv hdmi_phy_res \"480p59.94\"\n");

            writer.println("# setenv fb_x_res \"720\"");
            writer.println("# setenv fb_y_res \"576\"");
            writer.println("# setenv hdmi_phy_res \"576p50hz\"\n");

            writer.println("# setenv fb_x_res \"480\"");
            writer.println("# setenv fb_y_res \"800\"");
            writer.println("# setenv hdmi_phy_res \"480x800p60hz\"\n");

            writer.println("# setenv fb_x_res \"800\"");
            writer.println("# setenv fb_y_res \"480\"");
            writer.println("# setenv hdmi_phy_res \"800x480p60hz\"\n");

            writer.println("# setenv fb_x_res \"848\"");
            writer.println("# setenv fb_y_res \"480\"");
            writer.println("# setenv hdmi_phy_res \"848x480p60hz\"\n");

            writer.println("# setenv fb_x_res \"800\"");
            writer.println("# setenv fb_y_res \"600\"");
            writer.println("# setenv hdmi_phy_res \"600p60hz\"\n");

            writer.println("# setenv fb_x_res \"1024\"");
            writer.println("# setenv fb_y_res \"600\"");
            writer.println("# setenv hdmi_phy_res \"1024x600p60hz\"\n");

            writer.println("# setenv fb_x_res \"1024\"");
            writer.println("# setenv fb_y_res \"768\"");
            writer.println("# setenv hdmi_phy_res \"768p60hz\"\n");

            writer.println("# setenv fb_x_res \"1280\"");
            writer.println("# setenv fb_y_res \"720\"");
            writer.println("# setenv hdmi_phy_res \"720p50hz\"\n");

            writer.println("# setenv fb_x_res \"1280\"");
            writer.println("# setenv fb_y_res \"720\"");
            writer.println("# setenv hdmi_phy_res \"720p60hz\"\n");

            writer.println("# setenv fb_x_res \"1280\"");
            writer.println("# setenv fb_y_res \"768\"");
            writer.println("# setenv hdmi_phy_res \"1280x768p60hz\"\n");

            writer.println("# setenv fb_x_res \"1152\"");
            writer.println("# setenv fb_y_res \"864\"");
            writer.println("# setenv hdmi_phy_res \"1152x864p75hz\"\n");

            writer.println("# setenv fb_x_res \"1280\"");
            writer.println("# setenv fb_y_res \"800\"");
            writer.println("# setenv hdmi_phy_res \"800p59hz\"\n");

            writer.println("# setenv fb_x_res \"1280\"");
            writer.println("# setenv fb_y_res \"960\"");
            writer.println("# setenv hdmi_phy_res \"960p60hz\"\n");

            writer.println("# setenv fb_x_res \"1440\"");
            writer.println("# setenv fb_y_res \"900\"");
            writer.println("# setenv hdmi_phy_res \"900p60hz\"\n");

            writer.println("# setenv fb_x_res \"1440\"");
            writer.println("# setenv fb_y_res \"900\"");
            writer.println("# setenv hdmi_phy_res \"900p60hz\"\n");

            writer.println("# setenv fb_x_res \"1280\"");
            writer.println("# setenv fb_y_res \"1024\"");
            writer.println("# setenv hdmi_phy_res \"1024p60hz\"\n");

            writer.println("# setenv fb_x_res \"1400\"");
            writer.println("# setenv fb_y_res \"1050\"");
            writer.println("# setenv hdmi_phy_res \"1400x1050p60hz\"\n");

            writer.println("# setenv fb_x_res \"1360\"");
            writer.println("# setenv fb_y_res \"768\"");
            writer.println("# setenv hdmi_phy_res \"1360x768p60hz\"\n");

            writer.println("# setenv fb_x_res \"1600\"");
            writer.println("# setenv fb_y_res \"900\"");
            writer.println("# setenv hdmi_phy_res \"1600x900p60hz\"\n");

            writer.println("# setenv fb_x_res \"1600\"");
            writer.println("# setenv fb_y_res \"1200\"");
            writer.println("# setenv hdmi_phy_res \"1600x1200p60hz\"\n");

            writer.println("# setenv fb_x_res \"1920\"");
            writer.println("# setenv fb_y_res \"800\"");
            writer.println("# setenv hdmi_phy_res \"1920x800p60hz\"\n");

            writer.println("# setenv fb_x_res \"1792\"");
            writer.println("# setenv fb_y_res \"1344\"");
            writer.println("# setenv hdmi_phy_res \"1792x1344p60hz\"\n");

            writer.println("# setenv fb_x_res \"1920\"");
            writer.println("# setenv fb_y_res \"1080\"");
            writer.println("# setenv hdmi_phy_res \"1080i50hz\"\n");

            writer.println("# setenv fb_x_res \"1920\"");
            writer.println("# setenv fb_y_res \"1080\"");
            writer.println("# setenv hdmi_phy_res \"1080i60hz\"\n");

            writer.println("# setenv fb_x_res \"1920\"");
            writer.println("# setenv fb_y_res \"1080\"");
            writer.println("# setenv hdmi_phy_res \"1080p30hz\"\n");

            writer.println("# setenv fb_x_res \"1920\"");
            writer.println("# setenv fb_y_res \"1080\"");
            writer.println("# setenv hdmi_phy_res \"1080p50hz\"\n");

            writer.println("# setenv fb_x_res \"1920\"");
            writer.println("# setenv fb_y_res \"1080\"");
            writer.println("# setenv hdmi_phy_res \"1080p60hz\"\n");

            writer.println("# setenv fb_x_res \"1920\"");
            writer.println("# setenv fb_y_res \"1200\"");
            writer.println("# setenv hdmi_phy_res \"1920x1200p60hz\"\n");

            writer.println("setenv fb_x_res \"" + x_res +"\"");
            writer.println("setenv fb_y_res \"" + y_res +"\"");
            writer.println("setenv hdmi_phy_res \"" + mResolution +"\"\n");
            writer.println("setenv rotation \"" + mDegree +"\"\n");

            writer.println("# Enable/Disable ODROID-VU7 Touchsreen");
            writer.println("setenv disable_vu7 \"" + mDisablevu7.toString() +"\"\n");

            writer.println("setenv disable_dp \"" + mDisableDP.toString() +"\"\n");

            writer.println("# invert touch screen x,y");
            writer.println("setenv touch_invert_x \"" + mTouchInvertX.toString() +"\"");
            writer.println("setenv touch_invert_y \"" + mTouchInvertY.toString() +"\"\n");

            writer.println("setenv edid \"" + mEDID +"\"\n");
            writer.println("setenv hpd \"" + mHPD +"\"\n");
            writer.println("setenv mmc_size_gb \"-1\"\n");
            writer.println("get_mmc_size 0\n");
            writer.println("setenv led_blink        \"1\"\n");
            writer.println("setenv bootcmd      \"movi read kernel 0 40008000;bootz 40008000\"\n");
            writer.println("setenv bootargs     \"fb_x_res=${fb_x_res} fb_y_res=${fb_y_res} hdmi_phy_res=${hdmi_phy_res} disable_vu7=${disable_vu7} disable_dp=${disable_dp} touch_invert_x=${touch_invert_x} touch_invert_y=${touch_invert_y} edid=${edid} hpd=${hpd} led_blink=${led_blink} androidboot.mmc_size=${mmc_size_gb} androidboot.model=${board_name} androidboot.rotation=${rotation}\"");

            writer.println("boot");
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void reboot() {
        try {
            IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager
                    .getService(Context.POWER_SERVICE));
            pm.reboot(false, null, false);
        } catch (RemoteException e) {
            Log.e(TAG, "PowerManager service died!", e);
            return;
        }
    }

    public static void setMouse(String handed) {
        try {
            OutputStream stream;
            Process p = Runtime.getRuntime().exec("su");
            stream = p.getOutputStream();
            String cmd =  "setprop mouse.firstbutton " + handed;
            stream.write(cmd.getBytes());
            stream.flush();
            stream.close();

            Log.e(TAG, "setprop mouse.firstbutton " + handed);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void setValueToNode(String value, String node) {
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(node));
            out.write(value);
            out.newLine();
            out.close();
            Log.e(TAG, "set value : " + value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    protected String getFromNode(String node) {
        String value = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(node));
            value = bufferedReader.readLine();
            bufferedReader.close();
            Log.e(TAG, node + ", " + value);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected String getScaclingAvailableGovernor() {
        String value = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(SCALING_AVAILABLE_GOVERNORS));
            value = bufferedReader.readLine();
            bufferedReader.close();
            Log.e(TAG, value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }

    protected String getDRAMScaclingAvailableGovernor() {
        String value = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(DRAM_SCALING_AVAILABLE_GOVERNORS));
            value = bufferedReader.readLine();
            bufferedReader.close();
            Log.e(TAG, value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }

    protected String getDRAMScaclingAvailableFrequency() {
        String value = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(DRAM_SCALING_AVAILABLE_FREQUENCY));
            value = bufferedReader.readLine();
            bufferedReader.close();
            Log.e(TAG, value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }

    public native static String readFanMode();
    public native static void setFanMode(int auto_manual);
    public native static String readFanSpeeds();
    public native static void setFanSpeeds(String speeds);
    public native static String readPWMDuty();
    public native static void setPWMDuty(String duty);
    public native static String readPWMEnable();
    public native static void setPWMEnable(int enable);
    public native static String readTempLevels();
    public native static void setTempLevels(String temps);

    static {
        System.loadLibrary("fancontrol");
    }

    private static List<ApplicationInfo> appList = null;
    public static List<Intent> getAvailableAppList(Context context) {
        final PackageManager pm = context.getPackageManager();
        appList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<Intent> launchApps = new ArrayList<Intent>();
        for (ApplicationInfo appInfo: appList) {
            Intent launchApp = pm.getLaunchIntentForPackage(appInfo.packageName);
            if (launchApp != null)
                launchApps.add(launchApp);
        }

        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setPackage("home");
        launchApps.add(home);

        return launchApps;
    }

/*
    private void shortcutActivity () {
        final SharedPreferences pref = getSharedPreferences("utility", Context.MODE_PRIVATE);
        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        String pkg_f7 = pref.getString("shortcut_f7", null);
        String pkg_f8 = pref.getString("shortcut_f8", null);
        String pkg_f9 = pref.getString("shortcut_f9", null);
        String pkg_f10 = pref.getString("shortcut_f10", null);

        shortcut_f7 = (Spinner) findViewById(R.id.shortcut_f7);
        shortcut_f8 = (Spinner) findViewById(R.id.shortcut_f8);
        shortcut_f9 = (Spinner) findViewById(R.id.shortcut_f9);
        shortcut_f10 = (Spinner) findViewById(R.id.shortcut_f10);

        final List<Intent> appIntentList = getAvailableAppList(context);
        final ArrayList<String> appTitles = new ArrayList<String>();

        appTitles.add("No shortcut");
        for(Intent intent: appIntentList) {
            appTitles.add(intent.getPackage());
        }

        ApplicationAdapter adapter = new ApplicationAdapter(this, R.layout.applist_dropdown_item_1line, appTitles, appList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        shortcut_f7.setAdapter(adapter);
        shortcut_f8.setAdapter(adapter);
        shortcut_f9.setAdapter(adapter);
        shortcut_f10.setAdapter(adapter);

        shortcut_f7.setSelection(appTitles.indexOf(pkg_f7));
        shortcut_f8.setSelection(appTitles.indexOf(pkg_f8));
        shortcut_f9.setSelection(appTitles.indexOf(pkg_f9));
        shortcut_f10.setSelection(appTitles.indexOf(pkg_f10));

        OnItemSelectedListener listner = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long arg3) {
                SharedPreferences.Editor edit = pref.edit();
                int keycode = 0;

                switch (spinner.getId()) {
                    case R.id.shortcut_f7:
                        keycode = KeyEvent.KEYCODE_F7;
                        break;
                    case R.id.shortcut_f8:
                        keycode = KeyEvent.KEYCODE_F8;
                        break;
                    case R.id.shortcut_f9:
                        keycode = KeyEvent.KEYCODE_F9;
                        break;
                    case R.id.shortcut_f10:
                        keycode = KeyEvent.KEYCODE_F10;
                        break;
                }

                String shortcut_pref =
                        "shortcut_f" + ((keycode - KeyEvent.KEYCODE_F1)  + 1);

                if (position == 0) {
                    wm.setApplicationShortcut(keycode, null);
                    edit.putString(shortcut_pref, "No shortcut");
                }
                else{
                    wm.setApplicationShortcut(keycode, appIntentList.get(position - 1));
                    edit.putString(shortcut_pref, appIntentList.get(position - 1).getPackage());
                }
                edit.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        };

        shortcut_f7.setOnItemSelectedListener(listner);
        shortcut_f8.setOnItemSelectedListener(listner);
        shortcut_f9.setOnItemSelectedListener(listner);
        shortcut_f10.setOnItemSelectedListener(listner);

    }
*/
}
