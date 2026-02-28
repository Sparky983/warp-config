import { defineConfig } from "vitepress";

export default defineConfig({
  title: "Warp Config",
  cleanUrls: true,
  lastUpdated: true,
  markdown: {
    theme: {
      light: "material-theme-lighter",
      dark: "dark-plus",
    },
  },
  themeConfig: {
    nav: [
      {
        text: "API Reference",
        link: "https://javadoc.io/doc/me.sparky983.warp/warp",
      },
    ],
    sidebar: [
      {
        text: "Warp",
        items: [
          { text: "Getting Started", link: "/" },
          {
            text: "Mapping a Configuration",
            link: "/guides/mapping-a-configuration",
          },
          { text: "Custom Deserialiser", link: "/guides/custom-deserialiser" },
        ],
      },
      {
        text: "Extensions",
        items: [
          {
            text: "Adventure",
            link: "/extensions/adventure",
            description: "Support for `net.kyori:adventure` types",
          },
        ],
      },
    ],
    socialLinks: [
      { icon: "github", link: "https://github.com/Sparky983/warp-config" },
    ],
  },
});
