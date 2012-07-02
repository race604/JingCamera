/*
 * =====================================================================================
 *
 *       Filename:  filter.h
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  06/29/2012 03:38:42 PM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  YOUR NAME (), 
 *   Organization:  
 *
 * =====================================================================================
 */
#include <math.h>

inline int interpolation(float px,float py,int ll,int lh,int hl,int hh){
	 return (ll*(1-py) + hl*py) * (1-px)  +(lh*(1-py) + hh*py) * px;
}

inline int distanceSquare(int x1, int y1, int x2, int y2){
	x1 -= x2;
	y1 -= y2;
	return x1*x1 + y1*y1;
	
}

void llomo(uchar* rgba, int w, int h, int x, int y){

	float hw = w*0.6;
	float hh = h*0.6;
	
	for (int j=0; j<h; ++j){
		for (int i=0; i<w; ++i){

			float dx = (float)(i-x)/hw;
			float dy = (float)(j-y)/hh;
			float d = dx*dx + dy*dy;
			d = d*sqrt(d);

			float s = 1.0 - d;
			rgba[0] *= s;
			rgba[1] *= s;
			rgba[2] *= s;

			rgba+=4;

		}
	}

}


void spherize(uchar* rgba, int w, int h, int x, int y, int range, float scale){
	int size = std::min(w>>1,h>>1);
	size = range = std::min(size, range);
	int first = std::max(0, y-size);
	int last = std::min(h, y+size);
	int startX = std::max(0, x-size);
	int endX = std::min(w, x+size);

	int lenh = last - first;
	int lenw = endX - startX;
	size = lenh*lenw*4;

	uchar* temp = new uchar[size*4];
	
	uchar* p = temp;
	// 保存将要变化的区域的原像素
	for(int row=first; row<last; ++row, p+=lenw*4){
		memcpy(p, rgba + row*w*4 + startX*4, lenw*4*sizeof(uchar));	
	}

	float r2 = range*range;
	float ux, uy, k, px, py;
	int dx, dy;
	uchar* ll, *lh, *hl, *hh;

	for(int j=first; j<last; ++j){
		p = rgba + j*w*4 + startX*4;
		for(int i=startX; i<endX; ++i){
			dx = i-x;
			dy = j-y;
			int d2 = dx*dx+dy*dy;
			//k = (r2+d2-2*1.414f*range*sqrt(d2))/r2; 
			if (d2 < r2){
				k = scale * sqrt(d2/r2) + 1-scale;
				ux = k*dx + x - startX;
				uy = k*dy + y - first;

				if(ux<0) ux=0; else if(ux>=lenw-1) ux = lenw-2;
				if(uy<0) uy=0; else if(uy>=lenh-1) uy = lenh-2;

				px = ux - floor(ux);
				py = uy - floor(uy);

				ll= temp + (int)((int)(uy)*lenw+(int)(ux))*4;
				hl= temp + (int)((int)(uy+1)*lenw+(int)(ux))*4;

				p[0] = interpolation(px, py, ll[0], ll[4], hl[0], hl[4]) & 0xff; 
				p[1] = interpolation(px, py, ll[0+1], ll[4+1], hl[0+1], hl[4+1]) & 0xff; 
				p[2] = interpolation(px, py, ll[0+2], ll[4+2], hl[0+2], hl[4+2]) & 0xff; 
				//p[0] = ll[0]; 
				//p[1] = ll[1]; 
				//p[2] = ll[2]; 
			}
			p += 4;

		}
	}

	delete[] temp;
}


void relief(uchar* rgba, int w, int h){

	uchar* pre = new uchar[3];
	pre[0] = rgba[0];
	pre[1] = rgba[1];
	pre[2] = rgba[2];

	uchar* cur = new uchar[3];
	for(int j=0; j<h; ++j){
		for(int i=0; i<w; ++i){
			cur[0] = rgba[0];
			cur[1] = rgba[1];
			cur[2] = rgba[2];

			rgba[0] = 2*(cur[0] - pre[0])+ 127;
			rgba[1] = 2*(cur[1] - pre[1]) + 127;
			rgba[2] = 2*(cur[2] - pre[2]) + 127;

			pre[0] = cur[0];
			pre[1] = cur[1];
			pre[2] = cur[2];

			rgba += 4;

		}
	}

}

void sunshine(uchar* rgba, int w, int h, int x, int y, int raidus, int strength){

	int r2 = raidus*raidus;

	int startX = std::max(0, x-raidus);
	int endX = std::min(w, x+raidus);
	int startY = std::max(0, y-raidus);
	int endY = std::min(h, y+raidus);

	int dx, dy, delta;
	float d;

	uchar* p;

	for(int j=startY; j<endY; ++j){
		p = rgba + (j*w + startX)*4;
		for(int i=startX; i<endY; ++i){
			dx = i - x;
			dy = j - y;
			d = dx*dx + dy*dy;
			if ( d <= r2){
				delta = strength * ( 1.0 - sqrt(d)) / raidus;
				p[0] = std::min(255, p[0]+delta);
				p[1] = std::min(255, p[1]+delta);
				p[2] = std::min(255, p[2]+delta);
			}
			p += 4;
		}
	}

}

