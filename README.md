# Old Minecraft Enchantment Cracker

A tool for cracking and manipulating the RNG of enchanting in singleplayer Minecraft for versions between Beta 1.9 Prerelease 4 and 12w17a (1.3 snapshot) to always get the enchantments you want.<br><br>
This is possible as the Random object that selects the random words displayed in the Standard Galactic Alphabet is seeded by the Random object that is later used by the enchanting container to actually enchant your items.
Plug that data into the wonderful [LattiCG](https://github.com/mjtb49/LattiCG]), and presto, you now have the enchanting seed. Combine that with an enchantment predictor, and you have a pretty powerful tool.<br><br>
**This only works in singleplayer**, as the enchanting container used by the client and the "server" are the same object, thus the Random is the same. This breaks with the move to the internal server,
as the container object that seeded the enchanting GUI was no longer shared.<br><br>
If you have any issues, make sure you are running both Minecraft and the finder with the same Java version, as changes in Java's HashMap functions can influence the enchanting process.
If you still have problems, feel free to contact me on Discord @captain_s0l0, or open an issue.
