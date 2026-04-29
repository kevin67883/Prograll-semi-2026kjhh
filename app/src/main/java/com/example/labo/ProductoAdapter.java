package com.example.labo;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private Context context;
    private List<Producto> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditar(Producto p);
        void onEliminar(Producto p);
    }

    public ProductoAdapter(Context context, List<Producto> lista, OnItemClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Producto p = lista.get(position);
        h.tvNombre.setText(p.getNombre());
        h.tvMarca.setText(p.getMarca() + " | Talla: " + p.getTalla());
        h.tvPrecio.setText("$" + String.format("%.2f", p.getPrecio()));
        h.tvCodigo.setText("Cód: " + p.getCodigo());

        // ← nuevos: mostrar costo, ganancia y stock
        h.tvCosto.setText("Costo: $" + String.format("%.2f", p.getCosto()));
        h.tvGanancia.setText("Ganancia: " + String.format("%.1f", p.getGanancia()) + "%");
        h.tvStock.setText("Stock: " + p.getStock());

        h.tvSync.setText(p.isSincronizado() ? "✓ Sync" : "⏳ Pendiente");
        h.tvSync.setTextColor(p.isSincronizado() ?
                context.getColor(android.R.color.holo_green_light) :
                context.getColor(android.R.color.holo_orange_light));

        if (p.getFotoPath() != null && !p.getFotoPath().isEmpty()) {
            Glide.with(context)
                    .load(p.getFotoPath())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_ropa_placeholder)
                    .into(h.imgFoto);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView tvNombre, tvMarca, tvPrecio, tvCodigo, tvSync,
                tvCosto, tvGanancia, tvStock;   // ← nuevos
        ImageButton btnEditar, btnEliminar;

        ViewHolder(View v) {
            super(v);
            imgFoto    = v.findViewById(R.id.imgFoto);
            tvNombre   = v.findViewById(R.id.tvNombre);
            tvMarca    = v.findViewById(R.id.tvMarca);
            tvPrecio   = v.findViewById(R.id.tvPrecio);
            tvCodigo   = v.findViewById(R.id.tvCodigo);
            tvSync     = v.findViewById(R.id.tvSync);
            tvCosto    = v.findViewById(R.id.tvCosto);
            tvGanancia = v.findViewById(R.id.tvGanancia);
            tvStock    = v.findViewById(R.id.tvStock);
            btnEditar  = v.findViewById(R.id.btnEditar);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }
}