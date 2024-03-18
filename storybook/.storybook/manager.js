import { addons } from '@storybook/addons';
import {create} from "@storybook/theming";
import logo from "../assets/logo.png";

addons.setConfig({
    theme: create({
        base: 'light',
        brandTitle: 'Fixers D2',
        brandUrl: "https://komune-io.github.io/fixers-d2/",
        brandImage: logo,
    }),
    showToolbar: false
});
