//
//  BarChartView.swift
//  Charts
//
//  Copyright 2015 Daniel Cohen Gindi & Philipp Jahoda
//  A port of MPAndroidChart for iOS
//  Licensed under Apache License 2.0
//
//  https://github.com/danielgindi/Charts
//

import Foundation
import CoreGraphics
import DGCharts

/// Chart that draws bars.
open class RNRoundedBarChartView: BarChartView
{
    public override init(frame: CGRect)
    {
        super.init(frame: frame)
        initialize()
    }

    public required init?(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
        initialize()
    }

    internal func initialize()
    {
        renderer = RNRoundedBarChartRenderer(dataProvider: self, animator: chartAnimator, viewPortHandler: viewPortHandler)

        self.highlighter = BarHighlighter(chart: self)

        self.xAxis.spaceMin = 0.5
        self.xAxis.spaceMax = 0.5
    }

    func setBarRadius(_ radius: CGFloat){
        let customRenderer = renderer as! RNRoundedBarChartRenderer
        customRenderer.mRadius = radius
    }

}
