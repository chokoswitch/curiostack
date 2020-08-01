﻿using Google.Maps;
using Google.Maps.Feature.Style;
using UnityEngine;

namespace CafeMap.Map
{
    internal static class Constants
    {
        /// <summary>
        /// Default <see cref="GameObjectOptions"/> to put into
        /// <see cref="MapsService.LoadMap(Bounds,GameObjectOptions)"/> function.
        /// </summary>
        public static readonly GameObjectOptions DefaultGameObjectOptions;

        /// <summary>
        /// Setup default <see cref="GameObjectOptions"/>.
        /// </summary>
        static Constants()
        {
            // Find shaders that will be used to create required Materials for rendering geometry
            // generated by the Maps SDK for Unity.
            Shader standardShader = Shader.Find("Google/Maps/Shaders/Standard");

            if (standardShader == null)
            {
                // Try to find the Unity Standard Shader as a backup.
                standardShader = Shader.Find("Standard");

                if (standardShader == null)
                {
                    // Try to find the Legacy Diffuse Shader as a backup-backup.
                    standardShader = Shader.Find("Diffuse");

                    if (standardShader == null)
                    {
                        Debug.LogErrorFormat(
                            "Unable to find Maps SDK for Unity Standard Shader (named " +
                            "\"Google/Maps/Shaders/Standard\"), or as a backup the Unity Standard " +
                            "Shader (named \"Standard\"), or as a backup-backup the Legacy Unity " +
                            "Standard Shader (named \"Diffuse\"), so cannot setup default materials in {0}",
                            typeof(Constants));

                        return;
                    }

                    Debug.LogWarningFormat(
                        "Unable to find Maps SDK for Unity Standard Shader (named " +
                        "\"Google/Maps/Shaders/Standard\"), or as a backup the Unity Standard Shader " +
                        "(named  \"Standard\")\nDefaulting to Legacy Unity Standard Shader (named" +
                        "\"Diffuse\") as a backup-backup for setting up default materials in {0}",
                        typeof(Constants));
                }
                else
                {
                    Debug.LogWarningFormat(
                        "Unable to find Maps SDK for Unity Standard Shader (named " +
                        "\"Google/Maps/Shaders/Standard\").\nDefaulting to the Unity Standard Shader " +
                        "(named \"Standard\") as a backup for setting up default materials in {0}",
                        typeof(Constants));
                }
            }

            // Find BaseMaps Shader. Note that this Shader does not have a backup, as it has unique
            // behaviour needed for BaseMap level geometry to show in the correct render order.
            Shader baseMapShader = Shader.Find("Google/Maps/Shaders/BaseMap Color");

            if (baseMapShader == null)
            {
                Debug.LogErrorFormat(
                    "Unable to find Maps SDK for Unity Base Map Shader (named " +
                    "\"Google/Maps/Shaders/BaseMap Color\"), so unable to setup default materials in " +
                    "{0}",
                    typeof(Constants));

                return;
            }

            // Create default materials for use by buildings, as well as other materials for use by water,
            // ground, roads, etc.
            Material wallMaterial = new Material(standardShader) {color = new Color(1f, 0.75f, 0.5f)};

            Material roofMaterial = new Material(standardShader) {color = new Color(1f, 0.8f, 0.6f)};

            Material regionMaterial = new Material(baseMapShader)
            {
                color = new Color(0.5f, 0.7f, 0.5f),
            };
            regionMaterial.SetFloat("_Glossiness", 1f);

            Material waterMaterial = new Material(baseMapShader)
            {
                color = new Color(0.0f, 1.0f, 1.0f),
            };
            waterMaterial.SetFloat("_Glossiness", 1f);

            Material segmentMaterial = new Material(baseMapShader)
            {
                color = new Color(0.5f, 0.5f, 0.5f),
            };
            segmentMaterial.SetFloat("_Glossiness", 0.5f);

            // Create style for buildings made from extruded shapes (most buildings).
            ExtrudedStructureStyle extrudedStructureStyle =
                new ExtrudedStructureStyle
                        .Builder {WallMaterial = wallMaterial, RoofMaterial = roofMaterial}
                    .Build();

            // Create style for buildings with detailed vertex/triangle data (such as the Statue of
            // Liberty).
            ModeledStructureStyle modeledStructureStyle =
                new ModeledStructureStyle.Builder {Material = wallMaterial}.Build();

            // Create style for regions (such as parks).
            RegionStyle regionStyle = new RegionStyle.Builder {FillMaterial = regionMaterial}.Build();

            // Create style for bodies of water (such as oceans).
            AreaWaterStyle areaWaterStyle =
                new AreaWaterStyle.Builder {FillMaterial = waterMaterial}.Build();

            // Create style for lines of water (such as narrow rivers).
            LineWaterStyle lineWaterStyle =
                new LineWaterStyle.Builder {Material = waterMaterial}.Build();

            // Create style for segments (such as roads).
            SegmentStyle segmentStyle =
                new SegmentStyle.Builder {Material = segmentMaterial, Width = 7.0f}.Build();

            // Collect styles into a form that can be given to map loading function.
            DefaultGameObjectOptions = new GameObjectOptions
            {
                ExtrudedStructureStyle = extrudedStructureStyle,
                ModeledStructureStyle = modeledStructureStyle,
                RegionStyle = regionStyle,
                AreaWaterStyle = areaWaterStyle,
                LineWaterStyle = lineWaterStyle,
                SegmentStyle = segmentStyle,
            };
        }
    }
}