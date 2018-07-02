import { Host } from './host';
import { AgentType } from './agent-type';

export class Aid {
    constructor(public name: string, public host: Host, public type: AgentType) {}
}
