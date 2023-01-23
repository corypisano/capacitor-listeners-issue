export interface MyPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
