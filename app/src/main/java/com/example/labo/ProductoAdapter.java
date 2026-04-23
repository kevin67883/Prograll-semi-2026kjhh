package com.example.labo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final Context context;
    private List<Producto> lista;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditar(Producto p);
        void onEliminar(Producto p);
    }

    public ProductoAdapter(Context context, List<Producto> lista, OnItemClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_proucto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Producto p = lista.get(position);
        h.tvNombre.setText(p.getNombre());
        h.tvMarca.setText(p.getMarca() + " | Talla: " + p.getTalla());
        h.tvPrecio.setText("$" + p.getPrecio());
        h.tvCodigo.setText("Cod: " + p.getCodigo());

        if (p.getFotoPath() != null && !p.getFotoPath().isEmpty()) {
            h.imgFoto.setImageURI(Uri.parse(p.getFotoPath()));
        } else {
            h.imgFoto.setImageResource(R.drawable.ic_ropa_placeholder);
        }

        h.btnEditar.setOnClickListener(v -> listener.onEditar(p));
        h.btnEliminar.setOnClickListener(v -> listener.onEliminar(p));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public void actualizarLista(List<Producto> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView tvNombre, tvMarca, tvPrecio, tvCodigo;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View v) {
            super(v);
            imgFoto     = v.findViewById(R.id.imgFoto);
            tvNombre    = v.findViewById(R.id.tvNombre);
            tvMarca     = v.findViewById(R.id.tvMarca);
            tvPrecio    = v.findViewById(R.id.tvPrecio);
            tvCodigo    = v.findViewById(R.id.tvCodigo);
            btnEditar   = v.findViewById(R.id.btnEditar);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }
}