package com.smarternote.feature.sport

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.smarternote.core.base.activity.BaseActivity
import com.smarternote.core.base.activity.StatusBarBaseActivity
import com.smarternote.core.base.delegates.contentView
import com.smarternote.core.config.RouterPath
import com.smarternote.feature.sport.databinding.ActivityListBinding
import dagger.hilt.android.AndroidEntryPoint

@Route(path = RouterPath.Test.Matches)
@AndroidEntryPoint
class ListActivity : BaseActivity() {

    private val binding by contentView<ListActivity, ActivityListBinding>(R.layout.activity_list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        binding.toolbar.title = "列表"
        binding.toolbar.setOnBackListener {
            finish()
        }
    }
}