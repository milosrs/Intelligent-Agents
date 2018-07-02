import { Host } from './host';
import { AgentType } from './agent-type';

export class AgentTypeDTO {
    constructor(public agentType: AgentType, public host: Host) {}
}
