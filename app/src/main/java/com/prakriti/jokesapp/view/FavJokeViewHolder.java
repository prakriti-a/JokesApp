package com.prakriti.jokesapp.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prakriti.jokesapp.R;

import org.w3c.dom.Text;

public class FavJokeViewHolder extends RecyclerView.ViewHolder {
    // viewholder for each joke in fav jokes fragment
    // fave_joke_item.xml

    private TextView txtFavJoke;
    private ImageButton imgShareButton;

    public FavJokeViewHolder(@NonNull View itemView) {
        super(itemView);
        txtFavJoke = itemView.findViewById(R.id.txtFavJoke);
        imgShareButton = itemView.findViewById(R.id.shareButtonFavListItem);
    }

    public TextView getTxtFavJoke() {
        return txtFavJoke;
    }

    public void setTxtFavJoke(TextView txtFavJoke) {
        this.txtFavJoke = txtFavJoke;
    }

    public ImageButton getImgShareButton() {
        return imgShareButton;
    }

    public void setImgShareButton(ImageButton imgShareButton) {
        this.imgShareButton = imgShareButton;
    }

}
