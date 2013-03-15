/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package com.oxgcp.viewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

import com.oxgcp.viewPager.PagerView;
import android.app.Activity;
import android.os.Message;

@Kroll.proxy(creatableInModule=ViewpagerModule.class, propertyAccessors={
	// TiC.PROPERTY_SHOW_PAGING_CONTROL,
	// TiC.PROPERTY_SCROLL_TYPE
})
public class PagerViewProxy extends TiViewProxy
{
	private static final String TAG = "TiScrollableView";

	private static final int MSG_FIRST_ID = TiViewProxy.MSG_LAST_ID + 1;
	public static final int MSG_HIDE_PAGER = MSG_FIRST_ID + 101;
	public static final int MSG_MOVE_PREV = MSG_FIRST_ID + 102;
	public static final int MSG_MOVE_NEXT = MSG_FIRST_ID + 103;
	public static final int MSG_SCROLL_TO = MSG_FIRST_ID + 104;
	public static final int MSG_SET_VIEWS = MSG_FIRST_ID + 105;
	public static final int MSG_ADD_VIEW = MSG_FIRST_ID + 106;
	public static final int MSG_SET_CURRENT = MSG_FIRST_ID + 107;
	public static final int MSG_REMOVE_VIEW = MSG_FIRST_ID + 108;
	public static final int MSG_SET_ENABLED = MSG_FIRST_ID + 109;
	public static final int MSG_PAGE_MARGIN = MSG_FIRST_ID + 110;
	public static final int MSG_OFFSCREEN_PAGE_LIMIT = MSG_FIRST_ID + 113;
	public static final int MSG_CLIP_CHILDREN = MSG_FIRST_ID + 114;
	public static final int MSG_PAGE_WIDTH = MSG_FIRST_ID + 116;
	public static final int MSG_CONTENT_WIDTH = MSG_FIRST_ID + 117;
	public static final int MSG_CONTENT_HEIGHT = MSG_FIRST_ID + 118;
	public static final int MSG_HORIZONTAL_FADING_EDGE_ENABLED = MSG_FIRST_ID + 119;
	public static final int MSG_OVER_SCROLL_MODE = MSG_FIRST_ID + 120;
	public static final int MSG_LAST_ID = MSG_FIRST_ID + 999;
	
	private static final int DEFAULT_PAGING_CONTROL_TIMEOUT = 3000;

	protected AtomicBoolean inScroll;
	
	public PagerViewProxy()
	{
		super();
		inScroll = new AtomicBoolean(false);
		// defaultValues.put(TiC.PROPERTY_SHOW_PAGING_CONTROL, false);
		// defaultValues.put(TiC.PROPERTY_SCROLL_TYPE, 0);
	}

	public PagerViewProxy(TiContext context)
	{
		this();
	}

	@Override
	public TiUIView createView(Activity activity)
	{
		return new PagerView(this);
	}

	protected PagerView getView()
	{
		return (PagerView) getOrCreateView();
	}

	public boolean handleMessage(Message msg)
	{
		boolean handled = false;

		switch(msg.what) {
			case MSG_OVER_SCROLL_MODE: 
				getView().setOverScrollMode(msg.obj);
				break;
			case MSG_HORIZONTAL_FADING_EDGE_ENABLED: 
				getView().setHorizontalFadingEdgeEnabled(msg.obj);
				handled = true;
				break;
			case MSG_CONTENT_WIDTH:
				getView().setContentWidth(msg.obj);
				handled = true;
				break;
			case MSG_CONTENT_HEIGHT:
				getView().setContentHeight(msg.obj);
				handled = true;
				break;
			case MSG_PAGE_MARGIN:
				getView().setPageMargin(msg.obj);
				handled = true;
				break;
			case MSG_OFFSCREEN_PAGE_LIMIT:
				getView().setOffscreenPageLimit(msg.obj);
				handled = true;
				break;
			case MSG_CLIP_CHILDREN:
				getView().setClipChildren(msg.obj);
				handled = true;
				break;
			case MSG_MOVE_PREV:
				inScroll.set(true);
				getView().movePrevious();
				inScroll.set(false);
				handled = true;
				break;
			case MSG_MOVE_NEXT:
				inScroll.set(true);
				getView().moveNext();
				inScroll.set(false);
				handled = true;
				break;
			case MSG_SCROLL_TO:
				inScroll.set(true);
				getView().scrollTo(msg.obj);
				inScroll.set(false);
				handled = true;
				break;
			case MSG_SET_CURRENT:
				getView().setCurrentPage(msg.obj);
				handled = true;
				break;
			case MSG_SET_VIEWS: {
				AsyncResult holder = (AsyncResult) msg.obj;
				Object views = holder.getArg(); 
				getView().setViews(views);
				holder.setResult(null);
				handled = true;
				break;
			}
			case MSG_ADD_VIEW: {
				AsyncResult holder = (AsyncResult) msg.obj;
				Object view = holder.getArg();
				if (view instanceof TiViewProxy) {
					getView().addView((TiViewProxy) view);
					handled = true;
				} else if (view != null) {
					Log.w(TAG, "addView() ignored. Expected a Titanium view object, got " + view.getClass().getSimpleName());
				}
				holder.setResult(null);
				break;
			}
			case MSG_REMOVE_VIEW: {
				AsyncResult holder = (AsyncResult) msg.obj;
				Object view = holder.getArg(); 
				if (view instanceof TiViewProxy) {
					getView().removeView((TiViewProxy) view);
					handled = true;
				} else if (view != null) {
					Log.w(TAG, "removeView() ignored. Expected a Titanium view object, got " + view.getClass().getSimpleName());
				}
				holder.setResult(null);
				break;
			}
			case MSG_SET_ENABLED: {
				getView().setEnabled(msg.obj);
				handled = true;
				break;
			}
			default:
				handled = super.handleMessage(msg);
		}

		return handled;
	}

	@Kroll.getProperty @Kroll.method
	public Object getViews()
	{
		List<TiViewProxy> list = new ArrayList<TiViewProxy>();
		return getView().getViews().toArray(new TiViewProxy[list.size()]);
	}

	@Kroll.setProperty @Kroll.method
	public void setViews(Object viewsObject)
	{
		TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_VIEWS), viewsObject);
	}
	
	@Kroll.method
	public void addView(Object viewObject)
	{
		TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ADD_VIEW), viewObject);
	}

	@Kroll.method
	public void removeView(Object viewObject)
	{
		TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REMOVE_VIEW), viewObject);
	}

	@Kroll.method
	public void scrollToView(Object view)
	{
		if (inScroll.get()) return;

		getMainHandler().obtainMessage(MSG_SCROLL_TO, view).sendToTarget();
	}
	
	@Kroll.method
	public void setOverScrollMode(boolean overScrollMode)
	{
		getMainHandler().obtainMessage(MSG_OVER_SCROLL_MODE, overScrollMode).sendToTarget();
	}
	
	@Kroll.method
	public void	setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled)
	{
		getMainHandler().obtainMessage(MSG_HORIZONTAL_FADING_EDGE_ENABLED, horizontalFadingEdgeEnabled).sendToTarget();
	}
	
	@Kroll.method
	public void setOffscreenPageLimit(int offscreenPageLimit)
	{
		getMainHandler().obtainMessage(MSG_OFFSCREEN_PAGE_LIMIT, offscreenPageLimit).sendToTarget();
	}
	
	@Kroll.method
	public void setContentWidth(float contentWidth)
	{
		getMainHandler().obtainMessage(MSG_CONTENT_WIDTH, contentWidth).sendToTarget();
	}
	
	@Kroll.method
	public void setContentHeight(float contentHeight)
	{
		getMainHandler().obtainMessage(MSG_CONTENT_HEIGHT, contentHeight).sendToTarget();
	}
	
	@Kroll.method
	public void setPageMargin(int pageMargin)
	{
		getMainHandler().obtainMessage(MSG_PAGE_MARGIN, pageMargin).sendToTarget();
	}
	
	@Kroll.method
	public void setClipChildren(boolean clipChildren)
	{
		getMainHandler().obtainMessage(MSG_CLIP_CHILDREN, clipChildren).sendToTarget();
	}
	
	@Kroll.method
	public void movePrevious()
	{
		if (inScroll.get()) return;

		getMainHandler().removeMessages(MSG_MOVE_PREV);
		getMainHandler().sendEmptyMessage(MSG_MOVE_PREV);
	}

	@Kroll.method
	public void moveNext()
	{
		if (inScroll.get()) return;

		getMainHandler().removeMessages(MSG_MOVE_NEXT);
		getMainHandler().sendEmptyMessage(MSG_MOVE_NEXT);
	}

	public void fireDragEnd(int currentPage, TiViewProxy currentView) {
		if (hasListeners(TiC.EVENT_DRAGEND)) {
			KrollDict options = new KrollDict();
			options.put("view", currentView);
			options.put("currentPage", currentPage);
			fireEvent(TiC.EVENT_DRAGEND, options);
		}
		// TODO: Deprecate old event
		if (hasListeners("dragEnd")) {
			KrollDict options = new KrollDict();
			options.put("view", currentView);
			options.put("currentPage", currentPage);
			fireEvent("dragEnd", options);
		}
	}

	public void fireScrollEnd(int currentPage, TiViewProxy currentView)
	{
		if (hasListeners(TiC.EVENT_SCROLLEND)) {
			KrollDict options = new KrollDict();
			options.put("view", currentView);
			options.put("currentPage", currentPage);
			fireEvent(TiC.EVENT_SCROLLEND, options);
		}
		// TODO: Deprecate old event
		if (hasListeners("scrollEnd")) {
			KrollDict options = new KrollDict();
			options.put("view", currentView);
			options.put("currentPage", currentPage);
			fireEvent("scrollEnd", options);
		}
	}

	public void fireScroll(int currentPage, float currentPageAsFloat, TiViewProxy currentView)
	{
		if (hasListeners(TiC.EVENT_SCROLL)) {
			KrollDict options = new KrollDict();
			options.put("view", currentView);
			options.put("currentPage", currentPage);
			options.put("currentPageAsFloat", currentPageAsFloat);
			fireEvent(TiC.EVENT_SCROLL, options);
		}
	}

	@Kroll.setProperty @Kroll.method
	public void setScrollingEnabled(Object enabled)
	{
		getMainHandler().obtainMessage(MSG_SET_ENABLED, enabled).sendToTarget();
	}

	@Kroll.getProperty @Kroll.method
	public boolean getScrollingEnabled()
	{
		return getView().getEnabled();
	}

	@Kroll.getProperty @Kroll.method
	public int getCurrentPage()
	{
		return getView().getCurrentPage();
	}

	@Kroll.setProperty @Kroll.method
	public void setCurrentPage(Object page)
	{
		//getView().setCurrentPage(page);
		getMainHandler().obtainMessage(MSG_SET_CURRENT, page).sendToTarget();
	}

	@Override
	public void releaseViews()
	{
		getMainHandler().removeMessages(MSG_HIDE_PAGER);
		super.releaseViews();
	}
}