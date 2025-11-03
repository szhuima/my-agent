import { pluginReact } from '@rsbuild/plugin-react';
import { pluginLess } from '@rsbuild/plugin-less';
import { defineConfig } from '@rsbuild/core';
import MonacoEditorWebpackPlugin from 'monaco-editor-webpack-plugin';

export default defineConfig({
  plugins: [pluginReact(), pluginLess()],
  source: {
    entry: {
      index: './src/app.tsx',
    },
    /**
     * support inversify @injectable() and @inject decorators
     */
    decorators: {
      version: 'legacy',
    },
  },
  html: {
    title: '智能体开发平台',
  },
  tools: {
    webpack(config) {
      config.plugins = config.plugins || [];
      config.plugins.push(new MonacoEditorWebpackPlugin());
      return config;
    },
  },
});
