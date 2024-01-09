//
//  XAxisRenderer.swift
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

open class CustomXAxisRenderer: XAxisRenderer
{
    /// draws the x-labels on the specified y-position
    @objc open override func drawLabels(context: CGContext, pos: CGFloat, anchor: CGPoint)
    {
        guard let transformer = self.transformer else { return }

        let paraStyle = ParagraphStyle.default.mutableCopy() as! MutableParagraphStyle
        paraStyle.alignment = .center

        let labelAttrs: [NSAttributedString.Key : Any] = [.font: axis.labelFont,
                                                         .foregroundColor: axis.labelTextColor,
                                                         .paragraphStyle: paraStyle,
                                                          .backgroundColor: NSUIColor.white]

        let labelRotationAngleRadians = axis.labelRotationAngle * .pi / 180
        let isCenteringEnabled = axis.isCenterAxisLabelsEnabled
        let valueToPixelMatrix = transformer.valueToPixelMatrix

        var position = CGPoint.zero
        var labelMaxSize = CGSize.zero

        if axis.isWordWrapEnabled
        {
            labelMaxSize.width = axis.wordWrapWidthPercent * valueToPixelMatrix.a
        }

        let entries = axis.entries

        for i in entries.indices
        {
            let px = isCenteringEnabled ? CGFloat(axis.centeredEntries[i]) : CGFloat(entries[i])
            position = CGPoint(x: px, y: 0)
                .applying(valueToPixelMatrix)

            guard viewPortHandler.isInBoundsX(position.x) else { continue }

            let tickIndex = axis.entries[i]
            let distance = axis.entries.count > 1 ? axis.entries[1] - axis.entries[0] : 1

            let textBefore = axis.valueFormatter?.stringForValue(tickIndex - distance, axis: axis) ?? ""
            let textCurrent = axis.valueFormatter?.stringForValue(tickIndex, axis: axis) ?? ""

            let show = (textCurrent != "" && textCurrent != textBefore)

            let label = textCurrent
            let labelns = label as NSString

            if !show { continue }

            if axis.isAvoidFirstLastClippingEnabled
            {
                // avoid clipping of the last
                if i == axis.entryCount - 1 && axis.entryCount > 1
                {
                    let width = labelns.boundingRect(with: labelMaxSize, options: .usesLineFragmentOrigin, attributes: labelAttrs, context: nil).size.width

                    if width > viewPortHandler.offsetRight * 2.0,
                        position.x + width > viewPortHandler.chartWidth
                    {
                        position.x -= width / 2.0
                    }
                }
                else if i == 0
                { // avoid clipping of the first
                    let width = labelns.boundingRect(with: labelMaxSize, options: .usesLineFragmentOrigin, attributes: labelAttrs, context: nil).size.width
                    position.x += width / 2.0
                }
            }

            drawLabel(context: context,
                      formattedLabel: label,
                      x: position.x,
                      y: pos,
                      attributes: labelAttrs,
                      constrainedTo: labelMaxSize,
                      anchor: anchor,
                      angleRadians: labelRotationAngleRadians)
        }
    }

}
