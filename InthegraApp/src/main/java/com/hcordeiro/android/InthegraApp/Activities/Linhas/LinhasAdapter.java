package com.hcordeiro.android.InthegraApp.Activities.Linhas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.equalsp.stransthe.Linha;
import com.hcordeiro.android.InthegraApp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter de linha necessário para utilização de busca no listview
 * Created by hugo on 22/05/16.
 */
public class LinhasAdapter extends BaseAdapter {
    private Activity mContext;
    private List<Linha> linhasOriginal;
    private List<Linha> linhasTratadas;
    private LinhasFilter linhasFilter;

    public LinhasAdapter(Activity context, List<Linha> list) {
        mContext = context;
        linhasOriginal = list;
        linhasTratadas = list;
    }

    @Override
    public int getCount() {
        return linhasTratadas.size();
    }
    @Override
    public Object getItem(int pos) {
        return linhasTratadas.get(pos);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LinhasViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.simple_list_layout, null);
            viewHolder = new LinhasViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (LinhasViewHolder) v.getTag();
        }
        viewHolder.item.setText(linhasTratadas.get(position).toString());
        return v;
    }

    public Filter getLinhasFilter() {
        if (linhasFilter == null)
            linhasFilter = new LinhasFilter();
        return linhasFilter;
    }

    private void setList(ArrayList<Linha> data) {
        linhasTratadas = data;
        this.notifyDataSetChanged();
    }

    private class LinhasViewHolder {
        public TextView item;
        public LinhasViewHolder(View base) {
            item = (TextView) base.findViewById(R.id.item);
        }
    }

    private class LinhasFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<Linha> linhasFiltradas = new ArrayList<>();
                for (int i = 0; i < linhasOriginal.size(); i++) {
                    Linha linha = linhasOriginal.get(i);
                    if (linha.toString().toLowerCase().contains(constraint)) {
                        linhasFiltradas.add(linha);
                    }
                }
                result.count = linhasFiltradas.size();
                result.values = linhasFiltradas;
            } else {
                synchronized (this) {
                    result.values = linhasOriginal;
                    result.count = linhasOriginal.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                setList((ArrayList<Linha>) results.values);
            } else {
                setList((ArrayList<Linha>) linhasOriginal);
            }
        }
    }
}
