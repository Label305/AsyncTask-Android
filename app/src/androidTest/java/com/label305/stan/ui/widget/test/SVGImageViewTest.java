/*
 * |   _            _          _ ____   ___  _____   |
 * |  | |          | |        | |___ \ / _ \| ____|  |
 * |  | |      __ _| |__   ___| | __) | |_| | |__    |
 * |  | |     / _` | '_ \ / _ \ ||__ <|     |___ \   |
 * |  | |____| (_| | |_) |  __/ |___) |     |___) |  |
 * |  |______|\__,_|_.__/ \___|_|____/ \___/|____/   |
 *
 * @author Nick van den Berg <nick@label305.com>
 *
 * Copyright (c) 2013 Label305. All Right Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.label305.stan.ui.widget.test;

import android.test.AndroidTestCase;

import com.caverock.androidsvg.SVG;
import com.label305.stan.ui.widget.SVGImageView;


/**
 * Created by Label305 on 02/04/2014.
 */
public class SVGImageViewTest extends AndroidTestCase {

    SVGImageView mSvgImageView;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mSvgImageView = new SVGImageView(getContext());
        SVG svg;
        svg = new SVG();
        getContext().getResources().getLayout(com.label305.stan.test.R.layout.svg_imageview)
        mSvgImageView.setSVGResource(com.label305.stan.test.R.raw.ic_svg_test_square_red);
    }

    public void testTA() {
        fail();
    }

    public void testDoInvertSvg() throws Exception {

    }

    public void testDoNotInvertSvg() throws Exception {

    }

    public void testDoOverrideColors() throws Exception {

    }

    public void testDoNotOverrideColors() throws Exception {

    }

    public void testSetSvgColor() throws Exception {

    }

    public void testSetPressedSvgColor() throws Exception {

    }

    public void testSetIsPressable() throws Exception {

    }

    public void testSetIsNotPressable() throws Exception {

    }

    public void testSetSVGResource() throws Exception {

    }
}
