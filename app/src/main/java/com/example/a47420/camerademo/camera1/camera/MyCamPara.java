package com.example.a47420.camerademo.camera1.camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
 
import android.hardware.Camera;
import android.hardware.Camera.Size;
 
public class MyCamPara {
	private final CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static MyCamPara myCamPara = null;
	private static float previewRate = 1.3f;
	private MyCamPara(){
		
	}
	public static MyCamPara getInstance(){
		if(myCamPara == null){
			myCamPara = new MyCamPara();
			return myCamPara;
		}
		else{
			return myCamPara;
		}
	}
	
	public  Size getPreviewSize(List<Camera.Size> list, int th){
		Collections.sort(list, sizeComparator);
		Size size=null;
		for(int i=list.size()-1;i>=0;i--){
			size=list.get(i);
			if(size.width>th){
				break;
			}
		}
		previewRate = (float) size.width/size.height;
		return size;
	}
	public Size getPictureSize(List<Camera.Size> list, int th){
		Collections.sort(list, sizeComparator);
		Size size=null;
		for(int i=list.size()-1;i>=0;i--){
			size=list.get(i);
			if((size.width>th)&&equalRate(size, previewRate)){
				break;
			}
		}
		return size;
	}
	
	public boolean equalRate(Size s, float rate){
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.0002)
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	public  class CameraSizeComparator implements Comparator<Camera.Size>{
		//按升序排列
		@Override
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
			return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}
		
	}
}