package org.openremote.beta.client.flow;

import com.ait.lienzo.client.core.shape.Group;
import org.openremote.beta.shared.flow.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.openremote.beta.client.flow.Constants.PATCH_PADDING;
import static org.openremote.beta.client.flow.Constants.SLOT_PADDING;
import static org.openremote.beta.shared.flow.Slot.Type.SINK;

public abstract class Slots extends Group {
    private static final Logger LOG = Logger.getLogger(Slots.class.getName());

    final protected List<SlotShape> sinkSlots = new ArrayList<>();
    final protected List<SlotShape> sourceSlots = new ArrayList<>();
    protected double height;

    public void setSlots(double offsetBetweenSinkAndSource, Slot... slots) {
        removeAll();
        sinkSlots.clear();
        sourceSlots.clear();

        for (Slot slot : slots) {
            SlotShape slotShape = new SlotShape(slot);
            if (slot.getType() == SINK) {
                sinkSlots.add(slotShape);
            } else {
                sourceSlots.add(slotShape);
            }

            slotShape.addNodeMouseDownHandler(event -> createWireAttachedTo(slot, slotShape));
            slotShape.addNodeTouchStartHandler(event -> createWireAttachedTo(slot, slotShape));

            add(slotShape);
        }

        double sinksHeight = SLOT_PADDING;
        for (int i = 0; i < sinkSlots.size(); i++) {
            SlotShape slotShape = sinkSlots.get(i);
            slotShape.setX(slotShape.getX() - slotShape.getWidth() + min(SLOT_PADDING * 2, PATCH_PADDING));
            slotShape.setY(slotShape.getY() + SLOT_PADDING + (i * (slotShape.getHeight() + SLOT_PADDING)));
            sinksHeight += slotShape.getHeight() + SLOT_PADDING;
        }

        double sourcesHeight = SLOT_PADDING;
        for (int i = 0; i < sourceSlots.size(); i++) {
            SlotShape slotShape = sourceSlots.get(i);
            slotShape.setX(slotShape.getX() + offsetBetweenSinkAndSource - min(SLOT_PADDING * 2, PATCH_PADDING));
            slotShape.setY(slotShape.getY() + SLOT_PADDING + (i * (slotShape.getHeight() + SLOT_PADDING)));
            sourcesHeight += slotShape.getHeight() + SLOT_PADDING;
        }

        height = max(sinksHeight, sourcesHeight);
    }

    public double getHeight() {
        return height;
    }

    public SlotShape getSlotShape(String slotId) {
        for (SlotShape sinkSlot : sinkSlots) {
            if (sinkSlot.slot.getId().equals(slotId))
                return sinkSlot;
        }
        for (SlotShape sourceSlot : sourceSlots) {
            if (sourceSlot.slot.getId().equals(slotId))
                return sourceSlot;
        }
        return null;
    }

    protected void createWireAttachedTo(Slot slot, SlotShape slotShape) {
        double shapeX = getX() + slotShape.getX();
        double shapeY = getY() + slotShape.getY();
        WireShape wireShape;
        if (slot.getType() == SINK) {
            wireShape = createWireShape(shapeX, shapeY, shapeX, shapeY, null, slot);
            wireShape.getSinkHandle().setAttachedSlotShape(slotShape);
            wireShape.getSourceHandle().moveToTop();
        } else {
            wireShape = createWireShape(shapeX, shapeY, shapeX, shapeY, slot, null);
            wireShape.getSourceHandle().setAttachedSlotShape(slotShape);
            wireShape.getSinkHandle().moveToTop();
        }
    }

    protected abstract WireShape createWireShape(double x1, double y1, double x2, double y2, Slot source, Slot sink);

}
