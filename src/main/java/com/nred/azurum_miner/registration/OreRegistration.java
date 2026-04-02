package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.util.OreMaterial;
import com.nred.azurum_miner.util.OreMaterial.OreMaterialHasRaw;
import com.nred.azurum_miner.util.OreMaterial.OreMaterialHasShard;

import java.util.List;

public class OreRegistration {
    public static final OreMaterialHasShard Azurum = new OreMaterialHasShard("azurum", true);
    public static final OreMaterialHasRaw Thelxium = new OreMaterialHasRaw("thelxium", false);
    public static final OreMaterialHasRaw Galibium = new OreMaterialHasRaw("galibium", false);
    public static final OreMaterialHasRaw Palestium = new OreMaterialHasRaw("palestium", false);
    // TODO add 2 more ores, 1 in ground and 1 in T5

    public static final List<OreMaterial> ORE_MATERIALS = List.of(Azurum, Thelxium, Galibium, Palestium);

    public static void init() {
    }
}