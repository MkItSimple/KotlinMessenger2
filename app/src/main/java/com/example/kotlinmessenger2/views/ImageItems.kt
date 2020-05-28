package com.example.kotlinmessenger2.views

import com.example.kotlinmessenger2.R
import com.example.kotlinmessenger2.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_from_row.view.*
import kotlinx.android.synthetic.main.image_to_row.view.*

class ImageFromItem(val image: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val imageUri = image
        val imageviewFromRow = viewHolder.itemView.imageview_from_row
        Picasso.get().load(imageUri).into(imageviewFromRow)

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.image_from_row
    }
}

class ImageToItem(val image: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.textview_to_row.text = "IMAGE " + image
        val imageUri = image
        val imageviewToRow = viewHolder.itemView.imageview_to_row
        Picasso.get().load(imageUri).into(imageviewToRow)

        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.image_to_row
    }
}