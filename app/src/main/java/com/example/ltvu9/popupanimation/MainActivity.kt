package com.example.ltvu9.popupanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.RelativeLayout


class MainActivity : AppCompatActivity() {

    private lateinit var rootContainer: View

    private lateinit var tabSheetBackground: View
    private lateinit var tabSheetContent: View

    private lateinit var listSheetReveal: View
    private lateinit var listSheetContent: View

    private lateinit var bottomSheetBackground: View
    private lateinit var bottomSheetContent: View
    private lateinit var cancelIcon: View
    private lateinit var filterIcon: View

    private lateinit var fab: FloatingActionButton

    private var isSheetOpen = false

    private var isFilterFab = true

    private var isAnimate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViews()
    }

    private fun setUpViews() {
        rootContainer = findViewById<View>(R.id.rootContainer)

        tabSheetBackground = findViewById<View>(R.id.tabSheetBackground)
        tabSheetContent = findViewById<View>(R.id.tabSheetContent)

        listSheetReveal = findViewById<View>(R.id.listSheetReveal)
        listSheetContent = findViewById<View>(R.id.listSheetContent)

        bottomSheetBackground = findViewById<View>(R.id.bottomSheetBackground)
        bottomSheetContent = findViewById<View>(R.id.bottomSheetContent)

        cancelIcon = findViewById<View>(R.id.iv_cancel)
        filterIcon = findViewById<View>(R.id.iv_filter)

        fab = findViewById<FloatingActionButton>(R.id.fab).also {
            it.backgroundTintList = ColorStateList
                    .valueOf(ContextCompat.getColor(this, R.color.tabSheet))

            it.setOnClickListener {
                if(!isAnimate){
                    if(isFilterFab){
                        moveFabInCenterToOpenSheet()

                        isAnimate = true
                    }
                    else{
                        it.backgroundTintList = ColorStateList
                                .valueOf(ContextCompat.getColor(this, R.color.tabSheet))
                        fab.setImageDrawable(getDrawable(R.drawable.ic_filter))

                        isFilterFab = true
                    }
                }
            }
        }

        with(findViewById<View>(R.id.btnFilter)) {
            setOnClickListener {
                if (isSheetOpen && !isAnimate){
                    animateCloseSheet()

                    isSheetOpen = false
                    isAnimate = true
                }
            }
        }
    }

    private fun moveFabInCenterToOpenSheet() {
        val startX = fab.x
        val startY = fab.y
        val finalX = rootContainer.width / 2 - fab.width / 2
        val finalY = listSheetContent.y + listSheetContent.height / 2 - fab.height / 2

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.addUpdateListener {
            val factor = it.animatedValue as Float
            fab.x = startX + (finalX - startX) * factor
            fab.y = startY + (finalY - startY) * factor * factor
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                removeFabButtonBackground()

                moveFabFilterIconToBottomSheet()

                showTabSheet()

                revealListSheet()
            }
        })

        animator.duration = 300
        animator.start()
    }

    private fun removeFabButtonBackground() {
        fab.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        fab.elevation = 0f
    }

    private fun moveFabFilterIconToBottomSheet() {
        fab.animate()
                .y(bottomSheetContent.y + bottomSheetContent.height / 2 - fab.height / 2)
                .withEndAction {
                    slideCancelIconToRight()

                    slideFabFilterIconToRight()
                }
                .setDuration(200)
                .start()
    }

    private fun slideCancelIconToRight() {
        cancelIcon.visibility = View.VISIBLE
        cancelIcon.translationX = -bottomSheetContent.width / 4.0f
        cancelIcon.animate()
                .translationX(0f)
                .setDuration(200)
                .start()
    }

    private fun slideFabFilterIconToRight() {
        fab.animate()
                .x((bottomSheetContent.width * 3 / 4 - fab.width / 2).toFloat())
                .withEndAction {

                    fab.visibility = View.GONE

                    filterIcon.visibility = View.VISIBLE

                    showBottomSheetBackground()
                }
                .start()
    }

    private fun showBottomSheetBackground() {
        bottomSheetBackground.animate()
                .alpha(1f)
                .withEndAction {
                    isSheetOpen = true
                    isAnimate = false
                }
                .setDuration(400)
                .start()
    }

    private fun showTabSheet() {
        tabSheetBackground.visibility = View.VISIBLE
        tabSheetBackground.scaleY = 0f
        tabSheetBackground.animate()
                .scaleY(1f)
                .withEndAction {
                    tabSheetContent.visibility = View.VISIBLE
                }
                .setDuration(300)
                .setStartDelay(200)
                .start()
    }

    private fun revealListSheet() {
        listSheetReveal.visibility = View.VISIBLE

        val revealAnimator = ViewAnimationUtils.createCircularReveal(
                listSheetReveal,
                rootContainer.width / 2,
                (listSheetContent.x + listSheetContent.height / 2).toInt(),
                fab.width / 2.0f,
                listSheetReveal.height.toFloat() * 0.7f
        )
        revealAnimator.duration = 300
        revealAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                listSheetContent.visibility = View.VISIBLE
            }
        })
        revealAnimator.start()
    }

    private fun animateCloseSheet() {
        hideSheetViews()

        closeRevealListSheet()

        moveFabInCenterToCloseSheet()

        transformBottomSheetBackgroundToCircleAndMoveInCenter()
    }

    private fun hideSheetViews() {
        tabSheetBackground.visibility = View.INVISIBLE
        tabSheetContent.visibility = View.INVISIBLE

        listSheetContent.visibility = View.INVISIBLE

        cancelIcon.visibility = View.INVISIBLE
        filterIcon.visibility = View.INVISIBLE
    }

    private fun closeRevealListSheet() {

        val closeRevealAnimator = ViewAnimationUtils.createCircularReveal(
                listSheetReveal,
                rootContainer.width / 2,
                (listSheetContent.x + listSheetContent.height / 2).toInt(),
                listSheetReveal.height.toFloat() * 0.5f,
                fab.width / 2.0f

        )
        closeRevealAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                listSheetReveal.visibility = View.INVISIBLE
            }
        })
        closeRevealAnimator.duration = 300
        closeRevealAnimator.start()
    }

    private fun moveFabInCenterToCloseSheet() {

        val startX = bottomSheetContent.width / 4 - fab.width / 2
        val startY = fab.y
        val finalX = rootContainer.width / 2 - fab.width / 2
        val finalY = listSheetContent.y + listSheetContent.height / 2 - fab.height / 2

        fab.x = startX.toFloat()
        fab.y = startY

        fab.visibility = View.VISIBLE
        fab.setImageDrawable(getDrawable(R.drawable.ic_cancel))

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.addUpdateListener {
            val factor = it.animatedValue as Float
            fab.x = startX + (finalX - startX) * factor
            fab.y = startY + (finalY - startY) * factor * factor
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                showCancelFabBackground()

                rotateCancelFab()
            }
        })
        animator.duration = 300
        animator.start()
    }

    private fun showCancelFabBackground() {
        fab.backgroundTintList = ColorStateList
                .valueOf(ContextCompat.getColor(this, R.color.colorAccent))
        fab.elevation = 4.0f

        isFilterFab = false
    }

    private fun rotateCancelFab() {
        fab.animate()
                .rotationBy(360f)
                .withEndAction {
                    moveFabButtonToStartPosition()
                }
                .setDuration(1000)
                .start()
    }

    private fun moveFabButtonToStartPosition() {
        val startX = fab.x
        val startY = fab.y
        val finalX = rootContainer.width - fab.width - 16 * resources.displayMetrics.density
        val finalY = rootContainer.height - fab.height - 16 * resources.displayMetrics.density

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.addUpdateListener {
            val factor = it.animatedValue as Float
            fab.x = startX + (finalX - startX) * factor * factor
            fab.y = startY + (finalY - startY) * factor
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                isAnimate = false
            }
        })

        animator.duration = 300
        animator.start()
    }

    private fun transformBottomSheetBackgroundToCircleAndMoveInCenter() {

        val startY = bottomSheetBackground.y
        val finalY = listSheetContent.y + listSheetContent.height / 2 - fab.height / 2

        val startWidth = bottomSheetBackground.width
        val finalWidth = fab.width

        val startHeight = bottomSheetBackground.height
        val finalHeight = fab.height

        val background = bottomSheetBackground.background as GradientDrawable

        val params = bottomSheetBackground.layoutParams as RelativeLayout.LayoutParams

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            val factor = it.animatedValue as Float

            background.cornerRadius = 50 * resources.displayMetrics.density * factor

            params.width = (startWidth + (finalWidth - startWidth) * factor).toInt()
            params.height = (startHeight + (finalHeight - startHeight) * factor).toInt()

            bottomSheetBackground.y = startY + (finalY - startY) * factor

            bottomSheetBackground.requestLayout()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                resetBottomSheetBackground()
            }
        })
        animator.duration = 300
        animator.start()
    }

    private fun resetBottomSheetBackground() {

        val background = bottomSheetBackground.background as GradientDrawable
        val params = bottomSheetBackground.layoutParams as RelativeLayout.LayoutParams

        bottomSheetBackground.alpha = 0f

        background.cornerRadius = 0f

        params.width = bottomSheetContent.width
        params.height = bottomSheetContent.height

        // For some reasons bottomSheetBackground margin 8dp form bottom screen and I don't know why!!!
        bottomSheetBackground.y = bottomSheetContent.y + 8 * resources.displayMetrics.density

        bottomSheetBackground.requestLayout()
    }
}
