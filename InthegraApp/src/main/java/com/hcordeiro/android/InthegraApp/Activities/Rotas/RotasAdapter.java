package com.hcordeiro.android.InthegraApp.Activities.Rotas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.equalsp.stransthe.rotas.Rota;
import com.equalsp.stransthe.rotas.Trecho;
import com.hcordeiro.android.InthegraApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter de linha para utilziar a busca no listview
 * Created by hugo on 22/05/16.
 */
public class RotasAdapter extends BaseAdapter {
    private Activity mContext;
    private List<Rota> rotasOriginal;
    private List<Rota> rotasTratadas;
    private RotasFilter rotasFilter;

    public RotasAdapter(Activity context, List<Rota> list) {
        mContext = context;
        rotasOriginal = list;
        rotasTratadas = list;
    }

    @Override
    public int getCount() {
        return rotasTratadas.size();
    }
    @Override
    public Object getItem(int pos) {
        return rotasTratadas.get(pos);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        RotasViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.rotas_list_layout, null);
            viewHolder = new RotasViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (RotasViewHolder) v.getTag();
        }
        DecimalFormat df = new DecimalFormat("#.00");
        StringBuilder textBuilder = new StringBuilder();
        Trecho primeiroTrecho = rotasTratadas.get(position).getTrechos().get(0);

        Trecho segundoTrecho = rotasTratadas.get(position).getTrechos().get(1);
        textBuilder.append(segundoTrecho.getLinha().getDenomicao());
        textBuilder.append(", Distância até a parada: ");
        textBuilder.append(String.valueOf(df.format(primeiroTrecho.getDistancia())) + " m");

        viewHolder.itemRota.setText(textBuilder.toString());
        return v;
    }

    public Filter getRotasFilter() {
        if (rotasFilter == null)
            rotasFilter = new RotasFilter();
        return rotasFilter;
    }

    private void setList(ArrayList<Rota> data) {
        rotasTratadas = data;
        this.notifyDataSetChanged();
    }

    private class RotasViewHolder {
        public TextView itemRota;
        public RotasViewHolder(View base) {
            itemRota = (TextView) base.findViewById(R.id.itemRota);
        }
    }

    private class RotasFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<Rota> rotasFiltradas = new ArrayList<>();
                for (int i = 0; i < rotasOriginal.size(); i++) {
                    Rota rota = rotasOriginal.get(i);
                    if (rota.toString().toLowerCase().contains(constraint)) {
                        rotasFiltradas.add(rota);
                    }
                }
                result.count = rotasFiltradas.size();
                result.values = rotasFiltradas;
            } else {
                synchronized (this) {
                    result.values = rotasOriginal;
                    result.count = rotasOriginal.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                setList((ArrayList<Rota>) results.values);
            } else {
                setList((ArrayList<Rota>) rotasOriginal);
            }
        }
    }
}
