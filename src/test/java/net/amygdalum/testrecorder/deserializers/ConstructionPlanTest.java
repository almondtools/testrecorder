package net.amygdalum.testrecorder.deserializers;

import static org.hamcrest.Matchers.arrayContaining;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class ConstructionPlanTest {

    @Test
    public void testConstructionPlanOrderFewerParamsFirstConstructorParamsFirst() throws Exception {
        ConstructionPlan plan_3_2 = new ConstructionPlan(var(), constructorParams(3), setterParams(2));
        ConstructionPlan plan_2_3 = new ConstructionPlan(var(), constructorParams(2), setterParams(3));
        ConstructionPlan plan_2_2 = new ConstructionPlan(var(), constructorParams(2), setterParams(2));
        ConstructionPlan plan_1_2 = new ConstructionPlan(var(), constructorParams(1), setterParams(2));
        ConstructionPlan plan_1_1 = new ConstructionPlan(var(), constructorParams(1), setterParams(1));
        
        ConstructionPlan[] plans = new ConstructionPlan[]{
            plan_3_2,
            plan_1_1,
            plan_2_3,
            plan_2_2,
            plan_1_2
        };
        
        Arrays.sort(plans);
        
        assertThat(plans, arrayContaining(plan_1_1, plan_1_2, plan_2_2, plan_3_2, plan_2_3));
    }

    private LocalVariable var() throws Exception {
        return new LocalVariable("var");
    }

    private ConstructorParams constructorParams(int size) throws Exception {
        ConstructorParams params = Mockito.mock(ConstructorParams.class);
        when(params.size()).thenReturn(size);
        return params;
    }

    @SuppressWarnings("unchecked")
    private List<SetterParam> setterParams(int size) throws Exception {
        List<SetterParam> params = Mockito.mock(List.class);
        when(params.size()).thenReturn(size);
        return params;
    }

}
