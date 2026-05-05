#!/usr/bin/env node

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import axios from "axios";

const server = new Server(
  { name: "jira-xray-mcp", version: "1.0.0" },
  { capabilities: { tools: {} } }
);

// --- Config from env ---
const JIRA_BASE_URL = process.env.JIRA_BASE_URL || "";
const JIRA_EMAIL = process.env.JIRA_EMAIL || "";
const JIRA_API_TOKEN = process.env.JIRA_API_TOKEN || "";
const JIRA_PAT = process.env.JIRA_PAT || "";
const JIRA_PROJECT_KEY = process.env.JIRA_PROJECT_KEY || "";
const XRAY_PLATFORM = process.env.XRAY_PLATFORM || "cloud";
const XRAY_CLIENT_ID = process.env.XRAY_CLIENT_ID || "";
const XRAY_CLIENT_SECRET = process.env.XRAY_CLIENT_SECRET || "";

function getAuthHeaders() {
  if (JIRA_PAT) {
    return { Authorization: `Bearer ${JIRA_PAT}` };
  }
  if (JIRA_EMAIL && JIRA_API_TOKEN) {
    const encoded = Buffer.from(`${JIRA_EMAIL}:${JIRA_API_TOKEN}`).toString("base64");
    return { Authorization: `Basic ${encoded}` };
  }
  throw new Error("No Jira credentials configured. Set JIRA_API_TOKEN or JIRA_PAT.");
}

// --- Tools Definition ---
server.setRequestHandler(ListToolsRequestSchema, async () => ({
  tools: [
    {
      name: "fetch_jira_issue",
      description: "Fetch a single Jira issue by key (e.g., PROJ-123). Returns summary, description, acceptance criteria, attachments.",
      inputSchema: {
        type: "object",
        properties: {
          issue_key: { type: "string", description: "Jira issue key (e.g., PROJ-123)" },
          format: { type: "string", enum: ["json", "md"], description: "Output format: json or markdown", default: "md" }
        },
        required: ["issue_key"]
      }
    },
    {
      name: "fetch_jira_issues",
      description: "Fetch multiple Jira issues by project, type, or JQL query.",
      inputSchema: {
        type: "object",
        properties: {
          project: { type: "string", description: "Jira project key" },
          issue_type: { type: "string", description: "Issue type filter (Story, Bug, Task, Epic...)" },
          jql: { type: "string", description: "Custom JQL query (overrides project/type)" },
          max_results: { type: "number", description: "Max results to return", default: 20 },
          format: { type: "string", enum: ["json", "md"], description: "Output format", default: "md" }
        }
      }
    },
    {
      name: "fetch_epic_children",
      description: "Fetch all child issues (stories/tasks) of a Jira Epic.",
      inputSchema: {
        type: "object",
        properties: {
          epic_key: { type: "string", description: "Epic issue key (e.g., PROJ-10)" },
          format: { type: "string", enum: ["json", "md"], description: "Output format", default: "md" }
        },
        required: ["epic_key"]
      }
    },
    {
      name: "xray_authenticate",
      description: "Authenticate with Xray Cloud API and return a bearer token.",
      inputSchema: { type: "object", properties: {} }
    },
    {
      name: "xray_import_results",
      description: "Import test execution results to Xray (supports Playwright JSON, JUnit XML formats).",
      inputSchema: {
        type: "object",
        properties: {
          format: { type: "string", enum: ["playwright", "junit"], description: "Test result format" },
          results_json: { type: "string", description: "Test results content as JSON string" },
          project_key: { type: "string", description: "Jira project key for test execution" },
          test_plan_key: { type: "string", description: "Optional: Test Plan issue key to link" }
        },
        required: ["format", "results_json", "project_key"]
      }
    },
    {
      name: "test_jira_connection",
      description: "Test connectivity to Jira API. Returns server info if successful.",
      inputSchema: { type: "object", properties: {} }
    }
  ]
}));

// --- Tool Handlers ---
server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args } = request.params;

  try {
    switch (name) {
      case "fetch_jira_issue":
        return await handleFetchIssue(args);
      case "fetch_jira_issues":
        return await handleFetchIssues(args);
      case "fetch_epic_children":
        return await handleFetchEpicChildren(args);
      case "xray_authenticate":
        return await handleXrayAuth();
      case "xray_import_results":
        return await handleXrayImport(args);
      case "test_jira_connection":
        return await handleTestConnection();
      default:
        return { content: [{ type: "text", text: `Unknown tool: ${name}` }], isError: true };
    }
  } catch (error) {
    return {
      content: [{ type: "text", text: `Error: ${error.message}` }],
      isError: true
    };
  }
});

// --- Handlers ---

async function handleFetchIssue({ issue_key, format = "md" }) {
  const url = `${JIRA_BASE_URL}/rest/api/3/issue/${issue_key}`;
  const response = await axios.get(url, { headers: getAuthHeaders() });
  const issue = response.data;

  if (format === "json") {
    return { content: [{ type: "text", text: JSON.stringify(issue, null, 2) }] };
  }

  const md = formatIssueToMarkdown(issue);
  return { content: [{ type: "text", text: md }] };
}

async function handleFetchIssues({ project, issue_type, jql, max_results = 20, format = "md" }) {
  let query = jql;
  if (!query) {
    const parts = [];
    if (project) parts.push(`project = "${project}"`);
    if (issue_type) parts.push(`issuetype = "${issue_type}"`);
    query = parts.join(" AND ") || `project = "${JIRA_PROJECT_KEY}"`;
  }

  const url = `${JIRA_BASE_URL}/rest/api/3/search`;
  const response = await axios.get(url, {
    headers: getAuthHeaders(),
    params: { jql: query, maxResults: max_results, fields: "summary,description,status,issuetype,priority,assignee,labels" }
  });

  const issues = response.data.issues || [];

  if (format === "json") {
    return { content: [{ type: "text", text: JSON.stringify(issues, null, 2) }] };
  }

  const md = issues.map(formatIssueToMarkdown).join("\n---\n\n");
  return { content: [{ type: "text", text: `# Jira Issues (${issues.length} results)\n\n${md}` }] };
}

async function handleFetchEpicChildren({ epic_key, format = "md" }) {
  const jql = `"Epic Link" = "${epic_key}" OR parent = "${epic_key}" ORDER BY rank ASC`;
  return await handleFetchIssues({ jql, max_results: 50, format });
}

async function handleXrayAuth() {
  if (XRAY_PLATFORM === "cloud") {
    const response = await axios.post("https://xray.cloud.getxray.app/api/v2/authenticate", {
      client_id: XRAY_CLIENT_ID,
      client_secret: XRAY_CLIENT_SECRET
    });
    return { content: [{ type: "text", text: `✅ Xray Cloud authenticated. Token: ${response.data.substring(0, 20)}...` }] };
  }
  return { content: [{ type: "text", text: "✅ Xray Server uses Jira PAT — no separate auth needed." }] };
}

async function handleXrayImport({ format, results_json, project_key, test_plan_key }) {
  const token = await getXrayToken();

  let url;
  let contentType;

  if (XRAY_PLATFORM === "cloud") {
    if (format === "junit") {
      url = `https://xray.cloud.getxray.app/api/v2/import/execution/junit?projectKey=${project_key}`;
      contentType = "application/xml";
    } else {
      url = `https://xray.cloud.getxray.app/api/v2/import/execution?projectKey=${project_key}`;
      contentType = "application/json";
    }
  } else {
    url = `${JIRA_BASE_URL}/rest/raven/1.0/import/execution/${format === "junit" ? "junit" : ""}`;
    contentType = format === "junit" ? "application/xml" : "application/json";
  }

  const headers = {
    "Content-Type": contentType,
    ...(XRAY_PLATFORM === "cloud" ? { Authorization: `Bearer ${token}` } : getAuthHeaders())
  };

  if (test_plan_key) {
    url += `${url.includes("?") ? "&" : "?"}testPlanKey=${test_plan_key}`;
  }

  const response = await axios.post(url, results_json, { headers });
  return { content: [{ type: "text", text: `✅ Results imported successfully. Response: ${JSON.stringify(response.data)}` }] };
}

async function handleTestConnection() {
  const url = `${JIRA_BASE_URL}/rest/api/3/serverInfo`;
  const response = await axios.get(url, { headers: getAuthHeaders() });
  return { content: [{ type: "text", text: `✅ Connected to Jira: ${response.data.serverTitle || response.data.baseUrl}\nVersion: ${response.data.version || "Cloud"}` }] };
}

// --- Helpers ---

async function getXrayToken() {
  if (XRAY_PLATFORM !== "cloud") return null;
  const response = await axios.post("https://xray.cloud.getxray.app/api/v2/authenticate", {
    client_id: XRAY_CLIENT_ID,
    client_secret: XRAY_CLIENT_SECRET
  });
  return response.data;
}

function formatIssueToMarkdown(issue) {
  const fields = issue.fields || {};
  const key = issue.key;
  const summary = fields.summary || "No summary";
  const status = fields.status?.name || "Unknown";
  const type = fields.issuetype?.name || "Unknown";
  const priority = fields.priority?.name || "None";
  const assignee = fields.assignee?.displayName || "Unassigned";
  const description = extractTextFromADF(fields.description);

  return `## ${key}: ${summary}

| Field | Value |
|---|---|
| Type | ${type} |
| Status | ${status} |
| Priority | ${priority} |
| Assignee | ${assignee} |

### Description

${description || "_No description_"}
`;
}

function extractTextFromADF(adfNode) {
  if (!adfNode) return "";
  if (typeof adfNode === "string") return adfNode;
  if (adfNode.type === "text") return adfNode.text || "";
  if (adfNode.content && Array.isArray(adfNode.content)) {
    return adfNode.content.map(extractTextFromADF).join("");
  }
  return "";
}

// --- Start Server ---
const transport = new StdioServerTransport();
await server.connect(transport);
