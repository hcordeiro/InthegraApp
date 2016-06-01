package com.hcordeiro.android.InthegraApp.Util.GoogleMaps;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;
import com.hcordeiro.android.InthegraApp.R;

/**
 * http://stackoverflow.com/questions/37211274/google-map-marker-is-replaced-by-bounding-rectangle-on-zoom
 */
public class ParadaClusterRenderer extends DefaultClusterRenderer<ItemParadaClusterizavel> {
    private static final int CLUSTER_PADDING = 12;
//    private static final int ITEM_PADDING = 7;

    private final BitmapDescriptor paradaIcon;
    /*
        Como informado no stackoverflow, essa abordagem tem problemas de performance, sendo somente
        uma gambiarra para resolver o problema de renderização de ícones customizados.
         */
    private final IconGenerator mIconClusterGenerator;
    private final float mDensity;

    public ParadaClusterRenderer(Context context, GoogleMap map, ClusterManager<ItemParadaClusterizavel> clusterManager) {
        super(context, map, clusterManager);
        mDensity = context.getResources().getDisplayMetrics().density;

        mIconClusterGenerator = new CachedIconGenerator(context);
        mIconClusterGenerator.setContentView(makeSquareTextView(context, CLUSTER_PADDING));
        mIconClusterGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);

        paradaIcon = BitmapDescriptorFactory.fromResource(R.drawable.paradapointer);
    }

    @Override
    protected void onBeforeClusterItemRendered(ItemParadaClusterizavel item, MarkerOptions markerOptions) {
        markerOptions.icon(paradaIcon);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ItemParadaClusterizavel> cluster, MarkerOptions markerOptions) {
        int clusterSize = getBucket(cluster);
        mIconClusterGenerator.setBackground(makeClusterBackground(getColor(clusterSize)));
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(mIconClusterGenerator.makeIcon(getClusterText(clusterSize)));
        markerOptions.icon(descriptor);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ItemParadaClusterizavel> cluster) {
        return cluster.getSize() > 1;
    }

    private LayerDrawable makeClusterBackground(int color) {
        ShapeDrawable mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        mColoredCircleBackground.getPaint().setColor(color);
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(0x80ffffff);
        LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mColoredCircleBackground});
        int strokeWidth = (int) (mDensity * 3.0F);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }

    private SquareTextView makeSquareTextView(Context context, int padding) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(R.id.text);
        int paddingDpi = (int) (padding * mDensity);
        squareTextView.setPadding(paddingDpi, paddingDpi, paddingDpi, paddingDpi);
        return squareTextView;
    }
}
