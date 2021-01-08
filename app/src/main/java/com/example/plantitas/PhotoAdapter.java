package com.example.plantitas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{
    private Context mContext;
    private List<Uri>mListPhoto;

    public PhotoAdapter(Context mContext)
    {
        this.mContext=mContext;
    }
    public void setData(List<Uri>List)
    {
        this.mListPhoto=List;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
    Uri uri=mListPhoto.get(position);
    if (uri == null)
    {
        return;
    }
        try {

            Bitmap bitmap= MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),uri);
            String imagePath = uri.getPath();             // photoFile is a File class.
            Bitmap myBitmap  = BitmapFactory.decodeFile(imagePath);

            Bitmap orientedBitmap = ExifUtil.rotateBitmap(imagePath, myBitmap);
            if(bitmap!= null)
            {
                holder.imgPhoto.setImageBitmap(orientedBitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
       if(mListPhoto!=null)
       {
          return mListPhoto.size();
       }
       return 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imgPhoto;
        public PhotoViewHolder(@NonNull View itemView)
        {
            super(itemView);
                    imgPhoto=itemView.findViewById(R.id.img_photo);
        }

    }
}
