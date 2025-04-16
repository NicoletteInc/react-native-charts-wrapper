//
//  LineChartView.swift
//  Charts
//
//  Copyright 2015 Daniel Cohen Gindi & Philipp Jahoda
//  A port of MPAndroidChart for iOS
//  Licensed under Apache License 2.0
//
//  https://github.com/danielgindi/Charts
//
import CoreGraphics
import Foundation

#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif

import DGCharts

/// Chart that draws lines, surfaces, circles, ...
open class LineChartViewCustom: LineChartView
{
    private var _customViewPortEnabled = false

    /// The object representing the labels on the x-axis
    @objc open internal(set) lazy var secondXAxis = XAxis()

    /// The X axis renderer. This is a read-write property so you can set your own custom renderer here.
    /// **default**: An instance of XAxisRenderer
    @objc open lazy var secondXAxisRenderer = CustomXAxisRenderer(viewPortHandler: viewPortHandler, axis: secondXAxis, transformer: _secondAxisTransformer)

    internal var _secondAxisTransformer: Transformer!

    public override init(frame: CGRect)
    {
        super.init(frame: frame)
        _secondAxisTransformer = Transformer(viewPortHandler: viewPortHandler)
    }

    public required init?(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
        _secondAxisTransformer = Transformer(viewPortHandler: viewPortHandler)
    }

    open override func layoutSubviews() {
        super.layoutSubviews()
    }

    @objc open override func setViewPortOffsets(left: CGFloat, top: CGFloat, right: CGFloat, bottom: CGFloat)
    {
        super.setViewPortOffsets(left: left, top: top, right: right, bottom: bottom)
        prepareOffsetMatrix()
        prepareValuePxMatrix()
    }

    internal func prepareValuePxMatrix()
    {
        _secondAxisTransformer.prepareMatrixValuePx(chartXMin: xAxis.axisMinimum, deltaX: CGFloat(xAxis.axisRange), deltaY: CGFloat(rightAxis.axisRange), chartYMin: rightAxis.axisMinimum)
    }

    internal func prepareOffsetMatrix()
    {
        _secondAxisTransformer.prepareMatrixOffset(inverted: rightAxis.isInverted)
    }

    open override func notifyDataSetChanged(){
        super.notifyDataSetChanged()
        prepareOffsetMatrix()
        prepareValuePxMatrix()
    }

    open override func draw(_ rect: CGRect)
    {
        super.draw(rect)

        guard let data = data, renderer != nil else { return }

        let optionalContext = UIGraphicsGetCurrentContext()
        guard let context = optionalContext else { return }

        if secondXAxis.isEnabled {
            secondXAxis.calculate(min: data.xMin, max: data.xMax)
            secondXAxisRenderer.computeAxis(min: secondXAxis.axisMinimum, max: secondXAxis.axisMaximum, inverted: false)
            secondXAxisRenderer.renderAxisLine(context: context)
            secondXAxisRenderer.renderAxisLabels(context: context)
        }

    }

}
