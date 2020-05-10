<template id="new-task">
    <app-frame>
        <div class="row d-flex justify-content-center container">
            <div class="col-md-8">
                <div class="card-hover-shadow-2x mb-3 card">
                    <div class="card-header-tab card-header">
                        <div class="card-header-title font-size-lg font-weight-normal"><i class="fa fa-tasks"></i>&nbsp;Create a new task</div>
                    </div>
                    <div class="scroll-area-sm">
                        <perfect-scrollbar class="ps-show-limits">
                            <div style="position: static;" class="ps ps--active-y">
                                <div class="ps-content">
                                    <ul class=" list-group list-group-flush">
                                        <li class="list-group-item">
                                            <div class="todo-indicator bg-warning"></div>
                                            <div class="widget-content p-0">
                                                <div class="widget-content-wrapper">
                                                    <div class="widget-content-left">
                                                        <div class="form-group">
                                                            <label for="title">Task</label>
                                                            <input v-model="title" type="text" class="form-control" id="title" placeholder="insert your username">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="deadline">Deadline</label>
                                                            <input v-model="deadline" type="date" class="form-control" id="deadline" placeholder="insert your password">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </perfect-scrollbar>
                    </div>
                    <div class="d-block text-right card-footer"><button class="btn btn-primary" v-on:click="newTask()">Create</button></div>
                </div>
            </div>
        </div>


    </app-frame>
</template>
<script>
    Vue.component("new-task", {
        template: "#new-task",
        data: () => ({
            token: localStorage.getItem("token"),
            title: "",
            date: ""
        }),
        methods: {
            newTask() {
                let token = this.token;
                let title = this.title;
                let date = this.date;
                fetch("/api/me/tasks?token=" + token, {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "title=" + title
                })
                    .then(async res => { if(res.status === 200) location.pathname = "/tasks" })
                    .catch(() => alert("Error while creating new task"));
            }
        },
    });
</script>
