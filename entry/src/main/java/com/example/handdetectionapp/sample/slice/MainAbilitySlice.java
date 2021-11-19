package com.example.handdetectionapp.sample.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import com.example.handdetectionapp.sample.ResourceTable;

/**
 * MainAbilitySlice.
 */
public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_main_ability_slice);
        initComponents();
    }

    private void initComponents() {
        Component button = findComponentById(ResourceTable.Id_start_button);
        button.setClickedListener(component -> present(new ImageAbilitySlice(), new Intent()));
    }
}
