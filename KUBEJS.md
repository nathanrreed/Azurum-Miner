In kubejs/server_scripts:
``` js
ServerEvents.recipes(event => { })
```


Liquifier Recipe Template: 
``` js
	event.custom({
		"type": "azurum_miner:liquifier_recipe",
		"fluid_ingredient": { // fluid input
			"amount": 2000,
			"id": "minecraft:water"
		},
		"ingredient": {
			"item": "minecraft:quartz" //input
		},
		"power": 1.0, // Multiplier of baseEnergyRequired
		"processingTime": 60, // in ticks
		"result": {
			"amount": 500,
			"id": "azurum_miner:quartz_crystal_solution"
		}
	})
```

Crystallizer Recipe Template:
``` js
	event.custom({
		"type": "azurum_miner:crystallizer_recipe",
		"fluid_stack": { // fluid input
			"amount": 150,
			"id": "azurum_miner:quartz_crystal_solution"
		},
		"ingredient": { // item input
			"item": "azurum_miner:seed_crystal"
		},
		"power": 1.0, // Multiplier of baseEnergyRequired
		"processingTime": 60, // in ticks
		"rate": 0.5,
		"result": {
			"count": 2,
			"id": "minecraft:quartz"
		}
	})
```

Infuser Recipe Template:
``` js
	event.custom({
		"type": "azurum_miner:infuser_recipe",
		"catalyst": [], // optional catalyst (see below)
		"fluid_stack": { // fluid input
			"amount": 1000,
			"id": "azurum_miner:nether_essence"
		},
		"ingredient": {
			"item": "minecraft:deepslate"
		},
		"power": 1.0, // Multiplier of baseEnergyRequired
		"processingTime": 60, // in ticks
		"result": {
			"count": 1,
			"id": "minecraft:magma_block"
		}
	})
```
Infuser Recipe with second input Template:
``` js
	event.custom({
		"type": "azurum_miner:infuser_recipe",
		"catalyst": { // second item input
			"item": "minecraft:blue_ice"
		},
		"fluid_stack": { // fluid input
			"amount": 1000,
			"id": "minecraft:lava"
		},
		"ingredient": { // item input
			"item": "minecraft:ice"
		},
		"power": 1.0,
		"processingTime": 60, // in ticks
		"result": {
			"count": 9,
			"id": "minecraft:obsidian"
		}
	})
```
Transmogrifier Recipe Template:
``` js
	event.custom({
		"type": "azurum_miner:transmogrifier_recipe",
		"ingredient": { // item input
			"item": "minecraft:andesite"
		},
		"power": 1.0, // Multiplier of baseEnergyRequired
		"processingTime": 60, // in ticks
		"result": {
			"count": 1,
			"id": "minecraft:tuff"
		}
	})
```