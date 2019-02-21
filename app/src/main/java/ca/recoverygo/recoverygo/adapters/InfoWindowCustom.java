package ca.recoverygo.recoverygo.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

import ca.recoverygo.recoverygo.R;

public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public InfoWindowCustom(Context context) {
        this.context = context;
    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // R.layout.echo_info_window is a layout in my
        // res/layout folder. You can provide your own
        @SuppressLint("InflateParams") View v = Objects.requireNonNull(inflater).inflate(R.layout.activity_locator_info_window, null);

        TextView title = v.findViewById(R.id.info_window_title);
        TextView subtitle = v.findViewById(R.id.info_window_subtitle);
        title.setText(marker.getTitle());
        subtitle.setText(marker.getSnippet());
        return v;
    }
}
