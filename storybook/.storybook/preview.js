import React from "react";
import { ThemeContextProvider } from "@komune-io/g2-themes";
import { StorybookCanvas } from "@komune-io/g2-storybook-documentation";

import "./default.css";
import { CssBaseline } from "@mui/material";

export const parameters = {
  docs: {
    container: StorybookCanvas,
    components: {
      Canvas: StorybookCanvas,
    },
  },
  viewMode: "docs",
};

export const withThemeProvider = (Story) => {
  return (
    <ThemeContextProvider>
      <CssBaseline />
      <Story />
    </ThemeContextProvider>
  );
};

export const decorators = [withThemeProvider];
