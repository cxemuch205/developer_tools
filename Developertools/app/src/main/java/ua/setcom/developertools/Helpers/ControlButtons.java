package ua.setcom.developertools.Helpers;

import android.widget.TextView;

import ua.setcom.developertools.R;

/**
 * Created by daniil on 10/23/14.
 */
public enum ControlButtons {

    SHOW_LAYOUT_BUONDS(R.id.tb_show_layout_bounds, 0, false),
    OPEN_DEV_TOOL_SETTINGS(R.id.btn_open_dev_tool, 0, true),
    ENABLE_ADB_WIFI(R.id.tb_enable_adb_wifi, R.id.tv_ip_address, true),
    SHOW_POINT_TOUCHEBLE(R.id.tb_show_touchebles, 0, false);

    public int id;
    public int tvInfoId;
    public TextView tvInfo;
    public Object object;
    public boolean isEnable;

    ControlButtons(int btnId, int tvInfoId, boolean isEnable) {
        this.id = btnId;
        this.tvInfoId = tvInfoId;
        this.isEnable = isEnable;
    }
}
