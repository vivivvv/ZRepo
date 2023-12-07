package com.app.mybase.views.newslist.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.R
import com.app.mybase.databinding.NewsCardViewBinding
import com.app.mybase.helper.MySpannable
import com.app.mybase.helper.Utils
import com.app.mybase.model.Result
import javax.inject.Inject


class NewsAdapter @Inject constructor() :
    PagingDataAdapter<Result, NewsAdapter.ViewHolder>(differCallback) {

    private lateinit var binding: NewsCardViewBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = NewsCardViewBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(getItem(position)!!)
            }
        }
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        private var titleNews = binding.titleNews
        private var newsImage = binding.newsImage
        private var tvDescription = binding.tvDescription

        fun bind(item: Result) {
            titleNews.text = item.title
            // Update Image
            Utils.setImage(context.applicationContext, item.image_url, newsImage)
            tvDescription.text = item.summary
            makeTextViewResizable(tvDescription, 3, context.getString(R.string.read_more), true)
        }

        fun makeTextViewResizable(
            tv: TextView,
            maxLine: Int,
            expandText: String,
            viewMore: Boolean
        ) {
            if (tv.tag == null) {
                tv.tag = tv.text
            }
            val vto = tv.viewTreeObserver
            vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    tv.viewTreeObserver.removeOnPreDrawListener(this)
                    val lineCount = tv.layout.lineCount
                    if (lineCount > 0) {
                        val lineEndIndex: Int
                        if (maxLine == 0) {
                            lineEndIndex = tv.layout.getLineEnd(0)
                            val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                                .toString() + " " + expandText
                            tv.text = text
                            tv.movementMethod = LinkMovementMethod.getInstance()
                            tv.setText(
                                addClickablePartTextViewResizable(
                                    Utils.fromHtml(tv.text.toString()), tv, expandText,
                                    viewMore
                                ), TextView.BufferType.SPANNABLE
                            )
                        } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                            lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                            val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                                .toString() + " " + expandText
                            tv.text = text
                            tv.movementMethod = LinkMovementMethod.getInstance()
                            tv.setText(
                                addClickablePartTextViewResizable(
                                    Utils.fromHtml(tv.text.toString()), tv, expandText,
                                    viewMore
                                ), TextView.BufferType.SPANNABLE
                            )
                        } else {
                            lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                            val text =
                                tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                            tv.text = text
                            tv.movementMethod = LinkMovementMethod.getInstance()
                            tv.setText(
                                addClickablePartTextViewResizable(
                                    Utils.fromHtml(tv.text.toString()),
                                    tv,
                                    expandText,
                                    viewMore
                                ), TextView.BufferType.SPANNABLE
                            )
                        }
                    }
                    return true
                }
            })
        }

        private fun addClickablePartTextViewResizable(
            strSpanned: Spanned, tv: TextView,
            spanableText: String, viewMore: Boolean
        ): SpannableStringBuilder {
            val str = strSpanned.toString()
            val ssb = SpannableStringBuilder(strSpanned)
            if (str.contains(spanableText)) {
                ssb.setSpan(object : MySpannable(false) {
                    override fun onClick(widget: View) {
                        if (viewMore) {
                            tv.layoutParams = tv.layoutParams
                            tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                            tv.invalidate()
                            makeTextViewResizable(
                                tv,
                                -1,
                                context.getString(R.string.read_less),
                                false
                            )
                        } else {
                            tv.layoutParams = tv.layoutParams
                            tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                            tv.invalidate()
                            makeTextViewResizable(
                                tv,
                                3,
                                context.getString(R.string.read_more),
                                true
                            )
                        }
                    }
                }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
            }
            return ssb
        }
    }

    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<Result>() {
            override fun areItemsTheSame(
                oldItem: Result,
                newItem: Result
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Result,
                newItem: Result
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}