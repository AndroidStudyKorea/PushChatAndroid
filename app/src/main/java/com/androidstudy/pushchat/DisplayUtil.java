package com.androidstudy.pushchat;

public class DisplayUtil
{
	/**
	 * 픽셀단위를 현재 디스플레이 화면에 비례한 크기로 반환합니다.
	 * 
	 * @param pixel
	 * @return 변환된 값 (dp)
	 */
	public static int DPFromPixel(int px)
	{
		return (int)((px / DeviceUtil.mDispMetrics.density) + 0.5);
	}

	/**
	 * 현재 디스플레이 화면에 비례한 DP단위를 픽셀 크기로 반환합니다.
	 * 
	 * @param dp
	 * @return 변환된 값 (pixel)
	 */
	public static int PixelFromDP(int dp)
	{
		return (int)((dp * DeviceUtil.mDispMetrics.density) + 0.5);
	}
}