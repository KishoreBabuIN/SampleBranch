package com.kishorebabu.android.samplebranch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_share_deeplink.setOnClickListener {

            val branchUniversalObject = BranchUniversalObject()
                    .setCanonicalIdentifier(UUID.randomUUID().toString())
                    .setTitle("Content Title!")
                    .setContentDescription("Check out this Content!")
                    .addContentMetadata("content_id", "potato") //content id goes here
                    .setContentImageUrl("http://i.imgur.com/z6jhNiM.jpg") //a thumbnail preview for related image goes here
                    .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

            val linkProperties = LinkProperties()
                    .addControlParameter("\$desktop_url", "https://f22labs.com")
                    .addControlParameter("\$ios_url", "https://f22labs.com")
            showShareView(linkProperties, branchUniversalObject)
        }
    }

    private fun showShareView(linkProperties: LinkProperties, branchUniversalObject: BranchUniversalObject) {
        branchUniversalObject.generateShortUrl(this, linkProperties, Branch.BranchLinkCreateListener { url, error ->
            if (error == null) {
                Log.v(TAG, "\"got my Branch link to share: " + url)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, url)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(Intent.createChooser(intent, "Share trailer!"))
            } else {
                Log.e(TAG, "onLinkCreate: Branch error: " + error.message)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val branch = Branch.getInstance()

        branch.initSession({ branchUniversalObject, linkProperties, error ->
            if (error == null) {
                if (branchUniversalObject.metadata.isNotEmpty()
                        && branchUniversalObject.metadata.containsKey("content_id")) {
                    val contentId = branchUniversalObject.metadata.get("content_id")
                    Toast.makeText(this, "Deep Link Content ID: " + contentId, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.i(TAG, error.message)
            }
        }, this.intent.data, this)
    }

    override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }
}
