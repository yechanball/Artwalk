package com.a401.artwalk.view.route.list

import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.a401.artwalk.App
import com.a401.artwalk.BR
import com.a401.artwalk.BuildConfig
import com.a401.artwalk.R
import com.a401.artwalk.base.BaseViewHolder
import com.a401.artwalk.databinding.ItemRouteListBinding
import com.a401.domain.model.RouteForList
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class RouteListAdapter(
    private val startButtonClickListener: StartButtonClickListener,
    private val containerClickListener: (geometry: String) -> Unit,
) : ListAdapter<RouteListItem, BaseViewHolder<ItemRouteListBinding>>(RouteListItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemRouteListBinding> = BaseViewHolder(parent, R.layout.item_route_list)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRouteListBinding>, position: Int) {
        holder.binding.setVariable(BR.item, currentList[position])
        holder.binding.executePendingBindings()
        holder.binding.constraintLayoutRouteItemContainer.setOnClickListener() {
            containerClickListener(currentList[position].routeForList.geometry)
        }
        holder.binding.startButtonClickListener = startButtonClickListener
        holder.apply {
            val routeId = currentList[position].routeForList.routeId
            Glide.with(holder.itemView)
                .load(
                    GlideUrl(
                        BuildConfig.IMAGE_BASE_URL + routeId,
                        LazyHeaders.Builder().addHeader("accessToken", "Bearer ${App.prefs.getString("accessToken", "")}").build()
                    )
                )
                .transform(CenterCrop(), RoundedCorners(25))
                .into(binding.imageViewRouteItemThumbnail)
          }
    }
}

class StartButtonClickListener(val clickListener: (geometry: String) -> Unit) {
    fun onClick(routeForList: RouteForList) = clickListener(routeForList.geometry)
}