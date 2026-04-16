# Resource Remover

A lightweight mod that prevents selected resource files from being loaded by the game. You configure which resources to
block using two plain-text matcher files:

- `./config/resource_remover/assets.txt` — used for client resources (textures, models, sounds, lang, etc.)
- `./config/resource_remover/data.txt` — used for server/data resources (recipes, tags, loot tables, datapacks, etc.)

If a matcher file is missing or unreadable the mod will create an empty file automatically.

This mod is useful for:

- Cannot fully remove a resource file or annoying about loading errors when override with a blank file.
- Modpack developers who want to disable specific resources from mods without needing to create a full resource pack /
  data pack.

## How it works

- The mod wraps each resource with a filter that checks resource identifiers (ResourceLocation.toString()) against
  the matchers.
- When a resource matches a rule the mod prevents it from being returned to the game (and logs a short info message).
- Matching is applied separately for client assets (pack type CLIENT_RESOURCES → `assets.txt`) and server data (pack
  type SERVER_DATA → `data.txt`).

## Matcher rules and syntax

- Each non-empty line in `assets.txt` / `data.txt` is treated as a single rule. Blank lines and fully
  blank/whitespace-only lines are ignored.
- Lines starting with `#` are treated as comments and ignored.
- The implementation supports three rule modes (determined automatically):
    1. **Regular expression**: if the line contains regex metacharacters (one of . + ? | ( ) [ ] { } ^ $ \ ) it will be
       treated as a Java regular expression and matched against the whole resource string.
    2. **Wildcard**: if the rule contains `*` and no other regex meta characters, it will act as a simple wildcard where
       `*` matches any sequence of characters.
    3. **Exact match**: otherwise the rule is treated as an exact full-string match.

## Examples

- Resource identifiers are compared using their string form (for example `minecraft:models/block/dirt` or
  `mymod:textures/gui/button.png`). Use the examples below as guidance:

### Client (assets.txt) examples

```
# block models
minecraft:models/block/*

# specific texture
my_mod:textures/gui/button.png

# regex example: any PNG under any namespace ending with _icon.png
.*_icon\.png$
```

### Server/data (data.txt) examples

```
# all recipes in a namespace
my_mod:recipes/*

# exact datapack root file (if needed)
somepack:pack.mcmeta
```

## Notes on matching behavior

- A rule that contains any regex metacharacter is compiled as a full Java regex and matched against the whole resource
  string (`Pattern.matches` semantics used internally).
- A rule with `*` but no other meta characters is translated to a wildcard (so `mod:models/*/block` becomes a regex
  equivalent to `^mod:models/.*/block$`).
- If a rule is an invalid regex it will be treated as an exact literal match as a fallback.

## Troubleshooting

If your matchers do not appear to take effect, ensure:

- The files exist at `./config/resource_remover/` and are readable by the Minecraft process.
- Your patterns match the full ResourceLocation string form (`namespace:path` + path parts). Try a more general wildcard
  like `*` to verify the system is filtering.
- Check the game log for `Prevented loading of resource` messages to confirm matches.
