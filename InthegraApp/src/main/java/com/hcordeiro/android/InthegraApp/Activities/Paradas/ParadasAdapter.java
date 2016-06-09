package com.hcordeiro.android.InthegraApp.Activities.Paradas;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.rotas.ComparadorPorProximidade;
import com.equalsp.stransthe.rotas.PontoDeInteresse;
import com.hcordeiro.android.InthegraApp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter de parada necessário para utilização de busca no listview
 * Created by hugo on 22/05/16.
 */
public class ParadasAdapter extends BaseAdapter {
    private Activity mContext;
    private List<Parada> paradasOriginal;
    private List<Parada> paradasTratadas;
    private ParadasFilter paradasFilter;

    public ParadasAdapter(Activity context, List<Parada> list) {
        mContext = context;
        paradasOriginal = list;
        paradasTratadas = list;
    }


    public void sort(Location location) {
        if (location != null) {
            PontoDeInteresse pontoDeInteresse
                    = new PontoDeInteresse(location.getLatitude(), location.getLongitude());
            Collections.sort(paradasTratadas, new ComparadorPorProximidade(pontoDeInteresse));
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return paradasTratadas.size();
    }
    @Override
    public Object getItem(int pos) {
        return paradasTratadas.get(pos);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ParadasViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.paradas_list_layout, null);
            viewHolder = new ParadasViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ParadasViewHolder) v.getTag();
        }
        viewHolder.itemParada.setText(paradasTratadas.get(position).getEndereco());
        return v;
    }

    public Filter getParadasFilter() {
        if (paradasFilter == null)
            paradasFilter = new ParadasFilter();
        return paradasFilter;
    }

    private void setList(ArrayList<Parada> data) {
        paradasTratadas = data;
        this.notifyDataSetChanged();
    }

    private class ParadasViewHolder {
        public TextView itemParada;
        public ParadasViewHolder(View base) {
            itemParada = (TextView) base.findViewById(R.id.itemParada);
        }
    }

    private class ParadasFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<Parada> paradasFiltradas = new ArrayList<>();
                for (int i = 0; i < paradasOriginal.size(); i++) {
                    Parada parada = paradasOriginal.get(i);
                    StringBuilder builder = new StringBuilder();

                    if (parada.getDenomicao() != null) {
                        builder.append(parada.getDenomicao().toLowerCase());
                    }

                    if (parada.getEndereco() != null) {
                        builder.append(" " + parada.getEndereco().toLowerCase());
                    }

                    if (parada.getCodigoParada() != null) {
                        builder.append(" " + parada.getCodigoParada().toLowerCase());
                    }

                    if (builder.toString().contains(constraint)) {
                        paradasFiltradas.add(parada);
                    }
                }
                result.count = paradasFiltradas.size();
                result.values = paradasFiltradas;
            } else {
                synchronized (this) {
                    result.values = paradasOriginal;
                    result.count = paradasOriginal.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                setList((ArrayList<Parada>) results.values);
            } else {
                setList((ArrayList<Parada>) paradasOriginal);
            }
        }
    }

}
