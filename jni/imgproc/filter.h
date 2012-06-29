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
